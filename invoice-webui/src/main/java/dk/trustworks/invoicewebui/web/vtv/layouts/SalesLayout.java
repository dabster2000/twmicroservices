package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.ValoLightTheme;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.clients.SlackAPI;
import dk.trustworks.invoicewebui.repositories.AmbitionCategoryRepository;
import dk.trustworks.invoicewebui.repositories.AmbitionRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.stats.components.ConsultantsBudgetRealizationChart;
import dk.trustworks.invoicewebui.web.vtv.components.HoursPerConsultantChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class SalesLayout extends VerticalLayout {

    @Autowired
    private AmbitionRepository ambitionRepository;

    @Autowired
    private AmbitionCategoryRepository ambitionCategoryRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private HoursPerConsultantChart hoursPerConsultantChart;

    @Autowired
    private ConsultantsBudgetRealizationChart consultantsBudgetRealizationChart;

    @Autowired
    private SlackAPI slackAPI;

    private static Color[] colors = new ValoLightTheme().getColors();

    public SalesLayout() {
    }

    @Transactional
    public SalesLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();

        LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);

        Card consultantsBudgetRealizationCard = new Card();
        consultantsBudgetRealizationCard.getCardHolder().addComponent(consultantsBudgetRealizationChart.createConsultantsBudgetRealizationChart());

        Card hoursPerConsultantCard = new Card();
        hoursPerConsultantCard.getLblTitle().setValue("Consultant hours per month");
        DateTimeField field = new DateTimeField(event -> {
            hoursPerConsultantCard.getContent().removeAllComponents();
            hoursPerConsultantCard.getContent().addComponent(hoursPerConsultantChart.createHoursPerConsultantChart(event.getValue().toLocalDate().withDayOfMonth(1)));
            hoursPerConsultantCard.getLblTitle().setValue("Consultant hours per month (month norm: "+ ((DateUtils.getWeekdaysInPeriod(localDateStart, localDateStart.plusMonths(1))/5.0)*35.0) +")");
        });
        field.setWidth(150, Unit.PIXELS);
        field.setResolution(DateTimeResolution.MONTH);
        field.setValue(localDateStart.atStartOfDay());
        field.setDateFormat("MMM yyyy");
        field.addStyleName("floating");
        hoursPerConsultantCard.getHlTitleBar().addComponent(field);

        row.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(hoursPerConsultantCard);

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsBudgetRealizationCard);

        this.addComponent(responsiveLayout);

        getAverageAllocationByYear(LocalDate.of(2018, 6, 1));

        return this;
    }

    private Chart createOfferingChart(int year) {
        List<Work> workList = workRepository.findBillableWorkByPeriod(
                LocalDate.of(year, 1, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.of(year, 12, 31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        Map<String, AmbitionCategory> ambitionMap = new HashMap<>();
        List<Ambition> ambitionList = ambitionRepository.findAmbitionByOfferingIsTrueAndActiveIsTrue();
        List<AmbitionCategory> ambitionCategoryList = ambitionCategoryRepository.findByActiveTrue();
        for (Ambition ambition : ambitionList) {
            AmbitionCategory category = ambitionCategoryList.stream().filter(ambitionCategory -> ambitionCategory.getAmbitionCategoryType().equals(ambition.getCategory())).findFirst().get();
            ambitionMap.putIfAbsent(ambition.getName(), category);
        }


        Map<String, Double> hoursByOfferingsMap = new HashMap<>();
        Map<String, Double> hoursByOfferingCategoryMap = new HashMap<>();

        for (Work work : workList) {
            List<TaskOffering> taskOfferingList = work.getTask().getTaskOfferings();
            for (TaskOffering taskOffering : taskOfferingList) {
                hoursByOfferingsMap.putIfAbsent(taskOffering.getName(), 0.0);
                hoursByOfferingCategoryMap.putIfAbsent(ambitionMap.get(taskOffering.getName()).getName(), 0.0);
                double ambitionHours = hoursByOfferingsMap.get(taskOffering.getName());
                double categoryHours = hoursByOfferingCategoryMap.get(ambitionMap.get(taskOffering.getName()).getName());
                ambitionHours += work.getWorkduration() / taskOfferingList.size();
                categoryHours += work.getWorkduration() / taskOfferingList.size();
                hoursByOfferingsMap.replace(taskOffering.getName(), ambitionHours);
                hoursByOfferingCategoryMap.replace(ambitionMap.get(taskOffering.getName()).getName(), categoryHours);
            }
        }

        final Chart chart = new Chart(ChartType.COLUMN);
        chart.setId("chart");

        final Configuration conf = chart.getConfiguration();

        conf.setTitle("Offerings");
        conf.setSubTitle("Click the columns to view specific offerings. Click again to view offering categories.");
        conf.getLegend().setEnabled(false);

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Hours");
        conf.addyAxis(y);

        PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y +' hours'");

        conf.setPlotOptions(column);

        Tooltip tooltip = new Tooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
        tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f} hours</b> of total<br/>");
        conf.setTooltip(tooltip);

        DataSeries series = new DataSeries();
        series.setName("Offering categories");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        series.setPlotOptions(plotOptionsColumn);

        for (AmbitionCategory category : ambitionMap.values()) {
            DataSeriesItem item = new DataSeriesItem(category.getName(), Math.round(hoursByOfferingCategoryMap.getOrDefault(category.getName(), 0.0)));
            DataSeries drillSeries = new DataSeries(category.getName());
            drillSeries.setId(category.getAmbitionCategoryType());
            String[] ambitionNames = ambitionMap.keySet().stream().filter(s -> ambitionMap.get(s).getAmbitionCategoryType().equals(category.getAmbitionCategoryType())).toArray(String[]::new);
            List<Number> hoursByAmbition = new ArrayList<>();
            for (String ambitionName : ambitionNames) {
                hoursByAmbition.add(Math.round(hoursByOfferingsMap.getOrDefault(ambitionName, 0.0)));
            }

            drillSeries.setData(ambitionNames, hoursByAmbition.toArray(new Number[0]));
            series.addItemWithDrilldown(item, drillSeries);
        }

        conf.addSeries(series);

        return chart;
    }

    private void getAverageAllocationByYear(LocalDate startDate) {
        do {
            startDate = startDate.plusMonths(1);
            double count = 0.0;
            double allocation = 0.0;
            for (User user : userService.findEmployedUsersByDate(startDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                double billableWorkHours = statisticsService.getConsultantRevenueHoursByMonth(user, startDate);
                AvailabilityDocument availability = statisticsService.getConsultantAvailabilityByMonth(user, startDate);
                if (availability == null) {
                    availability = new AvailabilityDocument(user, startDate, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED);
                }
                double monthAllocation = 0.0;
                if (billableWorkHours > 0.0 && availability.getAvailableHours() > 0.0) {
                    monthAllocation = (billableWorkHours / availability.getAvailableHours()) * 100.0;
                    //System.out.println("--- startDate = " + startDate);
                    //System.out.println(user.getUsername()+" monthAllocation = " + monthAllocation);
                    count++;
                }
                allocation += monthAllocation;
            }
            //System.out.println("count = " + count);
            //System.out.println("allocation = " + allocation);
            //System.out.println(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " allocation = " + NumberUtils.round(allocation / count, 0));
            //User user = userService.findByUsername("hans.lassen");
            //slackAPI.sendSlackMessage(user, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " allocation = " + NumberUtils.round(allocation / count, 0));

        } while (startDate.isBefore(LocalDate.now()));
        //return NumberUtils.round(allocation / count, 0);
    }

    private static SolidColor color(int colorIndex) {
        SolidColor c = (SolidColor) colors[colorIndex];
        String cStr = c.toString().substring(1);

        int r = Integer.parseInt(cStr.substring(0, 2), 16);
        int g = Integer.parseInt(cStr.substring(2, 4), 16);
        int b = Integer.parseInt(cStr.substring(4, 6), 16);

        double opacity = (50 + new Random(0).nextInt(95 - 50)) / 100.0;

        return new SolidColor(r, g, b, opacity);
    }
}
