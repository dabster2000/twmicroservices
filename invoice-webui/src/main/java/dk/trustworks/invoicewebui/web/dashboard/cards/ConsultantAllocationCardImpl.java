package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.contexts.UserSession;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 11/08/2017.
 */
public class ConsultantAllocationCardImpl extends ConsultantAllocationCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;

    public ConsultantAllocationCardImpl(ContractService contractService, BudgetNewRepository budgetNewRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;



        //int monthPeriod = 12;
        //double[] monthTotalAvailabilites;
        //double[] monthAvailabilites;
        //monthTotalAvailabilites = new double[monthPeriod];
        //monthAvailabilites = new double[monthPeriod];
        //List<User> users = userRepository.findByActiveTrue();
        LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);
        LocalDate localDateEnd = LocalDate.now().withDayOfMonth(1).plusMonths(11);
        String[] monthNames = DateUtils.getMonthNames(localDateStart, localDateEnd);

        Chart chart = new Chart();
        chart.setWidth("100%");

        Configuration config = chart.getConfiguration();
        config.getChart().setType(ChartType.HEATMAP);
        config.getChart().setMarginTop(0);
        config.getChart().setMarginBottom(40);

        config.getTitle().setText("");

        config.getColorAxis().setMin(0);
        config.getColorAxis().setMax(100);
        config.getColorAxis().setMinColor(SolidColor.WHITE);
        config.getColorAxis().setMaxColor(new SolidColor("#3B8937"));

        config.getLegend().setLayout(LayoutDirection.VERTICAL);
        config.getLegend().setAlign(HorizontalAlign.RIGHT);
        config.getLegend().setMargin(0);
        config.getLegend().setVerticalAlign(VerticalAlign.TOP);
        config.getLegend().setY(25);
        config.getLegend().setSymbolHeight(10);
        config.getLegend().setEnabled(false);

        HeatSeries rs = new HeatSeries("% allocation");
        int userNumber = 0;

        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        Map<String, double[]> budgetRowList = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = localDateStart.plusMonths(i);

            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    double weeks = currentDate.getMonth().length(true) / 7.0;
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        if(!contractConsultant.getUser().getUuid().equals(user.getUuid())) continue;
                        budgetRowList.putIfAbsent(contract.getClient().getName(), new double[12]);
                        budgetRowList.get(contract.getClient().getName())[i] = (contractConsultant.getHours() * weeks) + budgetRowList.get(contract.getClient().getName())[i];
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            for (BudgetNew budget : budgets) {
                ContractConsultant contractConsultant = budget.getContractConsultant();
                if(!contractConsultant.getUser().getUuid().equals(user.getUuid())) continue;
                budgetRowList.putIfAbsent(contractConsultant.getContract().getClient().getName(), new double[12]);
                budgetRowList.get(contractConsultant.getContract().getClient().getName())[i] = (budget.getBudget() / budget.getContractConsultant().getRate()) + budgetRowList.get(contractConsultant.getContract().getClient().getName())[i];
            }
        }


            //budgetRowList.putIfAbsent(user.getUuid(), new double[12]);
        for (String key : budgetRowList.keySet()) {
            double[] hoursPerMonth = budgetRowList.get(key);


            LocalDate localDate = localDateStart;
            int m = 0;
            while (localDate.isBefore(localDateEnd) || localDate.isEqual(localDateEnd)) {
                List<UserStatus> userStatuses = user.getStatuses().stream().sorted(Comparator.comparing(UserStatus::getStatusdate)).collect(Collectors.toList());

                UserStatus userStatus = null;// = new UserStatus(null, null, LocalDate.now(), 0);
                for (UserStatus userStatusIteration : userStatuses) {
                    if (userStatusIteration.getStatusdate().isAfter(localDate)) break;
                    userStatus = userStatusIteration;
                }

                int weekDays = DateUtils.countWeekDays(localDate, localDate.plusMonths(1));
                assert userStatus != null;
                double budget = Math.round((weekDays * (userStatus.getAllocation() / 5.0)) - hoursPerMonth[m]);
                if (budget < 0.0) budget = 0.0;
                budget = Math.round(budget / Math.round(weekDays * (userStatus.getAllocation() / 5.0)) * 100.0);

                //monthAvailabilites[m] += Math.round(budget);
                //monthTotalAvailabilites[m] += 100;

                rs.addHeatPoint(m, userNumber, Math.round(100-budget));

                localDate = localDate.plusMonths(1);
                m++;
            }
            userNumber++;
        }

        config.getxAxis().setCategories(monthNames);
        config.getyAxis().setCategories(budgetRowList.keySet().toArray(new String[0]));
        config.getyAxis().setTitle("");

        PlotOptionsHeatmap plotOptionsHeatmap = new PlotOptionsHeatmap();
        plotOptionsHeatmap.setDataLabels(new DataLabels());
        plotOptionsHeatmap.getDataLabels().setEnabled(true);
        plotOptionsHeatmap.getStates().getHover().setFillColor(SolidColor.BLACK);

        SeriesTooltip tooltip = new SeriesTooltip();
        tooltip.setHeaderFormat("");
        tooltip.setPointFormat("Allocation: <b>{point.value}</b> %");
        plotOptionsHeatmap.setTooltip(tooltip);
        config.setPlotOptions(plotOptionsHeatmap);

        config.setSeries(rs);

        chart.drawChart(config);
        chart.setHeight(35*userNumber+40, Unit.PIXELS);
        this.getCardHolder().setHeight(35*userNumber+40, Unit.PIXELS);

        this.getCardHolder().addComponent(chart);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }

}
