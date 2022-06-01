package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.ValoLightTheme;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesHeatMap;
import dk.trustworks.invoicewebui.web.vtv.components.HoursPerConsultantChart;
import dk.trustworks.invoicewebui.web.vtv.components.UtilizationPerMonthChart;
import dk.trustworks.invoicewebui.web.vtv.model.MarginRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class SalesLayout extends VerticalLayout {

    @Autowired
    private BiService biService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private HoursPerConsultantChart hoursPerConsultantChart;

    @Autowired
    private UtilizationPerMonthChart utilizationPerMonthChart;

    @Autowired
    private SalesHeatMap salesHeatMap;

    public SalesLayout() {
    }

    @Transactional
    public SalesLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setFlexible();

        ResponsiveRow row = responsiveLayout.addRow().withGrow(true);

        LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);

        List<EmployeeAggregateData> employeeData = biService.getEmployeeAggregateDataByPeriod(localDateStart, localDateStart.plusMonths(2));
        for (int month = 0; month < 3; month++) {
            LocalDate actualDate = localDateStart.plusMonths(month);
            BoxImpl box = new BoxImpl().instance(new MLabel(DateUtils.stringIt(actualDate, "MMMM yyyy")+ " ( <=80% )").withStyleName("bold"));
            row.addColumn().withComponent(box).withDisplayRules(12,12,4,4);
            box.getContent().addComponent(new Label(" "));
            employeeData.stream().filter(e ->
                    e.getContractUtilization()<=0.8 &&
                            e.getStatusType().equals(StatusType.ACTIVE) &&
                            (e.getConsultantType().equals(ConsultantType.CONSULTANT) || e.getConsultantType().equals(ConsultantType.STUDENT)) &&
                            e.getMonth().withDayOfMonth(1).isEqual(actualDate))
                    .collect(Collectors.toList()).forEach(e -> {
                User user = userService.findByUUID(e.getUseruuid(), true);
                String teamname = e.getTeamMemberOf().size()>0?e.getTeamMemberOf().get(0).getShortname():"None";
                box.getContent().addComponent(new Label(user.getFirstname()+" "+user.getLastname()+" ("+teamname+") ("+ NumberUtils.round(e.getContractUtilization()*100, 0)+"%)"));
            });
        }

        Card hoursPerConsultantCard = new Card();
        hoursPerConsultantCard.getLblTitle().setValue("Consultant hours per month");
        OnOffSwitch onOffSwitch = new OnOffSwitch(true);
        DateTimeField field = new DateTimeField(event -> {
            reloadChart(event.getValue(), hoursPerConsultantCard, onOffSwitch.getValue());
        });
        onOffSwitch.addValueChangeListener(event -> {
            reloadChart(field.getValue(), hoursPerConsultantCard, event.getValue());
        });
        field.setWidth(150, Unit.PIXELS);
        field.setResolution(DateTimeResolution.MONTH);
        field.setValue(localDateStart.atStartOfDay());
        field.setDateFormat("MMM yyyy");
        field.addStyleName("floating");
        hoursPerConsultantCard.getHlTitleBar().addComponent(new MHorizontalLayout(new MLabel("Use adjusted budgets:"), onOffSwitch));
        hoursPerConsultantCard.getHlTitleBar().addComponent(new MLabel(" "));
        hoursPerConsultantCard.getHlTitleBar().addComponent(field);

        row.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(hoursPerConsultantCard);

        Card allocationChartCard = new Card();
        allocationChartCard.getLblTitle().setValue("Utilization per month");
        allocationChartCard.getContent().addComponent(utilizationPerMonthChart.createGroupUtilizationPerMonthChart(localDateStart.minusMonths(12), LocalDate.now().withDayOfMonth(1).plusMonths(11)));

        row.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(allocationChartCard);

        Card heatMapCard = new Card();
        heatMapCard.getCardHolder().addComponent(salesHeatMap.getChart(localDateStart, LocalDate.now().withDayOfMonth(1).plusMonths(11)));

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(heatMapCard);

        Card marginCard = new Card();

        List<MarginRow> marginRowList = new ArrayList<>();

        for (Contract contract : contractService.findActiveContractsByDate(LocalDate.now(), ContractStatus.SIGNED, ContractStatus.TIME, ContractStatus.BUDGET)) {
            for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                int margin = MarginService.get().calculateCapacityByMonthByUser(contractConsultant.getUseruuid(), (int) Math.round(contractConsultant.getRate()));
                marginRowList.add(new MarginRow(clientService.findOne(contract.getClientuuid()).getName(), contractConsultant.getUser().getUsername(), contractConsultant.getRate(), margin));
            }
        }

        Grid<MarginRow> grid = new Grid<>(MarginRow.class);
        grid.setItems(marginRowList);

        grid.setColumns("customer", "consultant", "rate", "margin");

        grid.setSizeFull();

        MVerticalLayout verticalLayout = new MVerticalLayout(grid).withFullWidth();

        marginCard.getContent().addComponent(verticalLayout);

        row.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(marginCard);

        this.addComponent(responsiveLayout);
        return this;
    }

    private void reloadChart(LocalDateTime event, Card hoursPerConsultantCard, Boolean onOffSwitch) {
        LocalDate date = event.toLocalDate().withDayOfMonth(1);
        hoursPerConsultantCard.getContent().removeAllComponents();
        hoursPerConsultantCard.getContent().addComponent(hoursPerConsultantChart.createHoursPerConsultantChart(date, userService.findEmployedUsersByDate(date, true, ConsultantType.CONSULTANT), onOffSwitch));
        hoursPerConsultantCard.getLblTitle().setValue("Consultant hours per month (month norm: "+ ((DateUtils.getWeekdaysInPeriod(date, date.plusMonths(1))/5.0)*35.0) +")");
    }
}

