package dk.trustworks.invoicewebui.web.knowledge.layout;

import com.google.gwt.user.client.ui.TreeItem;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.repositories.MicroCourseRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.knowledge.components.CertificationTable;
import dk.trustworks.invoicewebui.web.vtv.layouts.TenderManagementLayout;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.util.*;

@SpringComponent
@SpringUI
public class CkoAdministrationLayout extends VerticalLayout {

    @Autowired
    private CKOExpenseRepository ckoExpenseRepository;

    @Autowired
    private MicroCourseRepository microCourseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TenderManagementLayout tenderManagementLayout;

    public CkoAdministrationLayout init() {
        this.removeAllComponents();

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        this.addComponent(mainLayout);

        Component enlistedBox = createCourseQueueBox("ENLISTED", "Trustworks Academy - Course Queue");
        Component graduatedBox = createCourseQueueBox("GRADUATED", "Trustworks Academy - Graduated");

        ResponsiveRow row = mainLayout.addRow();

        ResponsiveLayout responsiveColumn1Layout = new ResponsiveLayout();
        ResponsiveLayout responsiveColumn2Layout = new ResponsiveLayout();
        row.addColumn().withDisplayRules(12,12,7,7).withComponent(responsiveColumn1Layout);
        row.addColumn().withDisplayRules(12,12,5,5).withComponent(responsiveColumn2Layout);

        ResponsiveRow leftRow = responsiveColumn1Layout.addRow();
        ResponsiveRow rightRow = responsiveColumn2Layout.addRow();

        leftRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(enlistedBox);
        leftRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(graduatedBox);
        Box box = new Box();
        box.getContent().addComponent(new CertificationTable(ckoExpenseRepository, userService).createCertificationsTable());
        leftRow.addColumn().withDisplayRules(12,12,12,12).withComponent(box);
        rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(getTotalYearlyBudgetChart());
        rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(getBudgetPerConsultantChart());

        return this;
    }

    private Component createCourseQueueBox(String type, String caption) {
        Box box = new Box();
        Tree<CkoTreeItem> tree = new Tree<>();
        TreeData<CkoTreeItem> data = new TreeData<>();
        tree.setItemDescriptionGenerator(CkoTreeItem::getDescription);
        tree.setItemCaptionGenerator(CkoTreeItem::getDescription);
        for (CkoCourse ckoCourse : microCourseRepository.findByActiveTrue()) {
            CkoTreeItem ckoParentTreeItem = new CkoTreeItem(ckoCourse.getName());
            data.addItem(null, ckoParentTreeItem);
            ckoCourse.getStudents().stream().filter(ckoCourseStudent -> ckoCourseStudent.getStatus().equals(type)).sorted(Comparator.comparing(CkoCourseStudent::getApplication)).forEach(ckoCourseStudent -> data.addItem(ckoParentTreeItem, new CkoTreeItem(ckoCourseStudent.getMember().getUsername()+" ("+ DateUtils.stringIt(ckoCourseStudent.getApplication())+")")));
        }

        tree.setDataProvider(new TreeDataProvider<>(data));
        box.getContent().addComponent(
                new MVerticalLayout(
                        new MLabel(caption).withStyleName("dark-grey-font"),
                        tree
                )
        );
        return box;
    }

    private Component getTotalYearlyBudgetChart() {
        Box box = new Box();
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setWidth(100, Unit.PERCENTAGE);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");

        ListSeries expenseSeries = new ListSeries("used");
        PlotOptionsColumn poc1 = new PlotOptionsColumn();
        poc1.setColor(new SolidColor("#DBEEEC"));
        expenseSeries.setPlotOptions(poc1);

        ListSeries availableSeries = new ListSeries("available");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#A3D3D2"));
        availableSeries.setPlotOptions(poc2);

        SortedMap<String, Integer> expensesPerYear = new TreeMap<>();
        for (CKOExpense ckoExpense : ckoExpenseRepository.findAll()) {
            if(ckoExpense.getStatus()!=null && ckoExpense.getStatus().equals(CKOExpenseStatus.WISHLIST)) continue;
            expensesPerYear.putIfAbsent(ckoExpense.getEventdate().getYear()+"", 0);
            Integer integer = expensesPerYear.get(ckoExpense.getEventdate().getYear() + "");
            expensesPerYear.replace(ckoExpense.getEventdate().getYear()+"", (integer+ckoExpense.getPrice()));
        }

        XAxis x = new XAxis();

        LocalDate startYear = LocalDate.of(2014,1,1);
        LocalDate endYear = LocalDate.of(LocalDate.now().getYear(), 12, 31);

        SortedMap<String, Integer> budgetsPerYear = new TreeMap<>();
        for (int year = startYear.getYear(); year <= endYear.getYear(); year++) {
            String yearAsString = year + "";
            int budgetPerYear = 0;
            for (int month = 1; month <= 12; month++) {
                int numberOfEmployedConsultants = userService.findEmployedUsersByDate(LocalDate.of(year, month, 1), true, ConsultantType.CONSULTANT).size();
                budgetPerYear += numberOfEmployedConsultants * 2000;
            }
            budgetsPerYear.put(yearAsString, budgetPerYear);

            x.addCategory(yearAsString);
            availableSeries.addData(budgetsPerYear.getOrDefault(yearAsString, 0) - expensesPerYear.getOrDefault(yearAsString, 0));
            expenseSeries.addData(expensesPerYear.getOrDefault(yearAsString, 0));
        }

        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Amount (kr)");
        StackLabels sLabels = new StackLabels(true);
        y.setStackLabels(sLabels);
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(100);
        legend.setY(70);
        legend.setFloating(true);
        legend.setShadow(true);
        //conf.setLegend(legend);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setStacking(Stacking.NORMAL);
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);
        conf.setPlotOptions(plot);

        conf.addSeries(expenseSeries);
        conf.addSeries(availableSeries);
        chart.drawChart(conf);
        box.getContent().addComponent(
                new MVerticalLayout(
                        new MLabel("Knowledge Budget per Year").withStyleName("dark-grey-font"),
                        chart
                )
        );
        return box;
    }

    private Component getBudgetPerConsultantChart() {
        Box box = new Box();
        Chart chart = new Chart(ChartType.BAR);
        chart.setWidth(100, Unit.PERCENTAGE);


        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");

        ListSeries expenseSeries = new ListSeries("used");
        PlotOptionsColumn poc1 = new PlotOptionsColumn();
        poc1.setColor(new SolidColor("#DBEEEC"));
        expenseSeries.setPlotOptions(poc1);

        ListSeries availableSeries = new ListSeries("available");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#A3D3D2"));
        availableSeries.setPlotOptions(poc2);

        SortedMap<String, Integer> expensesPerConsultant = new TreeMap<>();
        for (User user : userService.findCurrentlyEmployedUsers(true, ConsultantType.CONSULTANT)) {
            int totalExpenses = ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid()).stream().filter(ckoExpense -> ckoExpense.getEventdate().getYear() == LocalDate.now().getYear()).mapToInt(CKOExpense::getPrice).sum();
            expensesPerConsultant.put(user.getUsername(), totalExpenses);
        }

        XAxis x = new XAxis();
        //x.setTitle("year");

        SortedMap<String, Integer> budgetsPerConsultant = new TreeMap<>();
        for (User user : userService.findCurrentlyEmployedUsers(false, ConsultantType.CONSULTANT)) {
            Optional<UserStatus> firstStatus = user.getStatuses().stream().filter(userStatus -> userStatus.getStatus().equals(StatusType.ACTIVE)).min(Comparator.comparing(UserStatus::getStatusdate));
            if (!firstStatus.isPresent()) continue;

            int monthsNotEmployed = DateUtils.countMonthsBetween(LocalDate.of(LocalDate.now().getYear(), 1, 1), firstStatus.get().getStatusdate().withDayOfMonth(1));
            int maxBudgetFirstYear = 24000 - (monthsNotEmployed<0?0:(monthsNotEmployed * 2000));
            Optional<UserStatus> lastStatus = user.getStatuses().stream().max(Comparator.comparing(UserStatus::getStatusdate)); // last status
            if(lastStatus.isPresent() && lastStatus.get().getStatus().equals(StatusType.TERMINATED)) {
                maxBudgetFirstYear = expensesPerConsultant.get(user.getUsername());
            }

            budgetsPerConsultant.put(user.getUsername(), maxBudgetFirstYear);

            x.addCategory(user.getUsername());
            availableSeries.addData(budgetsPerConsultant.getOrDefault(user.getUsername(), 0) - expensesPerConsultant.getOrDefault(user.getUsername(), 0));
            expenseSeries.addData(expensesPerConsultant.getOrDefault(user.getUsername(), 0));
        }

        chart.setHeight(budgetsPerConsultant.size() * 23 + 250, Unit.PIXELS);

        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Amount (kr)");
        StackLabels sLabels = new StackLabels(false);
        y.setStackLabels(sLabels);
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(100);
        legend.setY(70);
        legend.setFloating(true);
        legend.setShadow(true);
        legend.setReversed(true);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setStacking(Stacking.NORMAL);
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);
        conf.setPlotOptions(plot);

        conf.addSeries(expenseSeries);
        conf.addSeries(availableSeries);
        chart.drawChart(conf);
        box.getContent().addComponent(
                new MVerticalLayout(
                        new MLabel("Knowledge Budget per Consultant "+LocalDate.now().getYear()).withStyleName("dark-grey-font"),
                        chart
                )
        );
        return box;
    }
}

@Data
@NoArgsConstructor
@RequiredArgsConstructor
class CkoTreeItem {
    String id = UUID.randomUUID().toString();
    @NonNull String description;
}