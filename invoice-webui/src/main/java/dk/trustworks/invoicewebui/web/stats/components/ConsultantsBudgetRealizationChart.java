package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ConsultantsBudgetRealizationChart {

    private final GraphKeyValueRepository graphKeyValueRepository;
    private final StatisticsService statisticsService;
    private final ContractService contractService;
    private final WorkService workService;
    private final BudgetNewRepository budgetNewRepository;
    private final UserService userService;

    @Autowired
    public ConsultantsBudgetRealizationChart(GraphKeyValueRepository graphKeyValueRepository, StatisticsService statisticsService, ContractService contractService, WorkService workService, BudgetNewRepository budgetNewRepository, UserService userService) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.statisticsService = statisticsService;
        this.contractService = contractService;
        this.workService = workService;
        this.budgetNewRepository = budgetNewRepository;
        this.userService = userService;
    }

    public Chart createConsultantsBudgetRealizationChart() {
        LocalDate periodStart = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEnd = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Consultant Budget Realization for "+ periodStart.format(DateTimeFormatter.ofPattern("MMM yyyy")));
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);


        List<GraphKeyValue> amountPerItemList = statisticsService.findConsultantBillableHoursByPeriod(periodStart, periodEnd).stream().sorted(Comparator.comparing(GraphKeyValue::getDescription)).collect(Collectors.toList());

        String[] categories = new String[amountPerItemList.size()+5];
        DataSeries revenueList = new DataSeries("Realization");

        Map<String, Double> budgetPerUser = new HashMap<>();

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);//, LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            //double budgetSum = 0.0;
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    //double weeks = currentDate.getMonth().maxLength() / 7.0;
                    for (ContractConsultant consultant : contract.getContractConsultants()) {
                        double weeks = workService.getWorkDaysInMonth(consultant.getUser().getUuid(), currentDate) / 5.0;
                        List<Work> workList = workService.findByPeriodAndUserUUID(
                                currentDate.withDayOfMonth(1),
                                currentDate.withDayOfMonth(currentDate.lengthOfMonth()),
                                consultant.getUser().getUuid());
                        double notWork = 0.0;
                        for (Work work : workList) {
                            if(work.getTask().getUuid().equals("02bf71c5-f588-46cf-9695-5864020eb1c4") ||
                                    work.getTask().getUuid().equals("f585f46f-19c1-4a3a-9ebd-1a4f21007282")) notWork += work.getWorkduration();
                        }
                        addBookingToMap(budgetPerUser, consultant, weeks, notWork);
                        //budgetSum += ((consultant.getHours() * weeks) - notWork) * consultant.getRate();
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            for (BudgetNew budget : budgets) {
                addBudgetPerUser(budgetPerUser, budget);
            }
        }

        DataSeries budgetSeries = new DataSeries("Budget");

        Map<String, Double> budgetPerUserMap = prepareNamedMap(budgetPerUser);

        int j = 0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            revenueList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            double v = budgetPerUserMap.getOrDefault(amountPerItem.getDescription(), 0.0) - amountPerItem.getValue();
            if(v<0.0) v=0.0;
            budgetSeries.add(new DataSeriesItem(amountPerItem.getDescription(), v));
            StringBuilder shortname = new StringBuilder();
            for (String s : amountPerItem.getDescription().split(" ")) {
                shortname.append(s.charAt(0));
            }
            categories[j++] = shortname.toString();
        }
/*
        for (String s : budgetPerUserMap.keySet()) {
            budgetSeries.add(new DataSeriesItem(s, budgetPerUserMap.get(s)));
        }
        */
/*
        for (String s : budgetPerUserNoWorkMap.keySet()) {
            revenueList.add(new DataSeriesItem(s, 0.0));
            budgetSeries.add(new DataSeriesItem(s, budgetPerUserNoWorkMap.get(s)));
            StringBuilder shortname = new StringBuilder();
            for (String s2 : s.split(" ")) {
                shortname.append(s2.charAt(0));
            }
            System.out.println("shortname = " + shortname.stringIt());
            categories[j++] = shortname.stringIt();
        }
        */


        //budgetSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(budgetSum)));
        //categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.PERCENT);
        chart.getConfiguration().setPlotOptions(plotOptions);

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(budgetSeries);
        chart.getConfiguration().addSeries(revenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private Map<String, Double> prepareNamedMap(Map<String, Double> budgetPerUser) {
        Map<String, Double> budgetPerUserMap = new TreeMap<>();
        for (String useruuid : budgetPerUser.keySet()) {
            User user = userService.findByUUID(useruuid);
            String userFullname = user.getFirstname() + " " + user.getLastname();
            Double budget = budgetPerUser.get(useruuid);
            if (budget < 0.0) budget = 0.0;
            budgetPerUserMap.put(userFullname, budget);
        }
        return budgetPerUserMap;
    }

    private void addBudgetPerUser(Map<String, Double> prebookingPerUser, BudgetNew budget) {
        prebookingPerUser.putIfAbsent(budget.getContractConsultant().getUser().getUuid(), 0.0);
        Double aDouble = Math.floor(prebookingPerUser.get(budget.getContractConsultant().getUser().getUuid()) + budget.getBudget() / budget.getContractConsultant().getRate());
        prebookingPerUser.replace(budget.getContractConsultant().getUser().getUuid(), aDouble);
    }

    private void addBookingToMap(Map<String, Double> prebookingPerUser, ContractConsultant consultant, double weeks, double notWork) {
        prebookingPerUser.putIfAbsent(consultant.getUser().getUuid(), 0.0);
        Double aDouble = Math.floor(prebookingPerUser.get(consultant.getUser().getUuid()) + ((consultant.getHours() * weeks) - notWork));
        prebookingPerUser.replace(consultant.getUser().getUuid(), aDouble);
    }

}
