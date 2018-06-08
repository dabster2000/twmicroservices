package dk.trustworks.invoicewebui.web.admin.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.common.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class AdminManagerImpl extends VerticalLayout {

    @Autowired
    private CountEmployeesJob countEmployees;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoCardImpl userInfoCard;

    @Autowired
    private UserSalaryCardImpl userSalaryCard;

    @Autowired
    private UserStatusCardImpl userStatusCard;

    @Autowired
    private UserPhotoCardImpl userPhotoCard;

    @Autowired
    private RoleCardImpl roleCard;

    ResponsiveLayout responsiveLayout;
    ComboBox<User> userComboBox;
    ResponsiveRow contentRow;

    public AdminManagerImpl() {
    }

    @Transactional
    @PostConstruct
    public void init() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        MButton addUserButton = new MButton("add user").withStyleName("flat", "friendly").withListener((Button.ClickListener) event -> {
            User user = new User();
            user.setUsername("new.new");
            userRepository.save(user);
            userComboBox.setItems(userRepository.findAll());
            userComboBox.setSelectedItem(user);
        });

        userComboBox = new ComboBox<>();
        userComboBox.setItems(userRepository.findByOrderByUsername());
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        responsiveLayout.addRow()
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(new MHorizontalLayout().withFullWidth().add(userComboBox).add(addUserButton));
        userComboBox.addValueChangeListener(event -> {
            contentRow.removeAllComponents();
            loadData();
        });
        contentRow = responsiveLayout.addRow();
        contentRow.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(getChart());

        Card card = new Card();
        card.getContent().addComponent(getGrid());
        contentRow.addColumn().withDisplayRules(12, 12, 12, 12)
                .withComponent(card);
        addComponent(responsiveLayout);
    }

    protected Component getChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        LocalDate periodStart = LocalDate.of(2014, 02, 01);
        LocalDate periodEnd = LocalDate.now();
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Employee Growth");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Employees");

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            List<User> usersByLocalDate = countEmployees.getUsersByLocalDate(currentDate);
            revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")), usersByLocalDate.size()));
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private Grid<Employee> getGrid() {
        Grid<Employee> grid = new Grid<>();
        grid.setSizeFull();

        ArrayList<Employee> employees = new ArrayList();
        // Set the data provider (ListDataProvider<CompanyBudgetHistory>)
        for (User user : userRepository.findAll()) {
            Optional<Salary> salary = user.getSalaries().stream().sorted(Comparator.comparing(Salary::getActivefrom).reversed()).findFirst();
            if(!salary.isPresent()) continue;
            Optional<UserStatus> userStatus = user.getStatuses().stream().sorted((Comparator.comparing(UserStatus::getStatusdate)).reversed()).findFirst();
            if(!userStatus.isPresent()) continue;
            Employee employee = new Employee(user.getFirstname() + " " + user.getLastname(),
                    userStatus.get().getStatus().name(),
                    userStatus.get().getAllocation(),
                    salary.get().getSalary());
            employees.add(employee);
        }

        ListDataProvider dataProvider = new ListDataProvider(employees);
        grid.setDataProvider(dataProvider);

        // Set the selection mode
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addColumn(Employee::getName).setCaption("Name");
        grid.addColumn(Employee::getStatus).setCaption("Status");
        grid.addColumn(Employee::getHours).setCaption("Hours");
        grid.addColumn(Employee::getSalary).setCaption("Salary");


        //HeaderRow topHeader = grid.prependHeaderRow();


        DecimalFormat dollarFormat = new DecimalFormat("$#,##0.00");
/*
        IntStream.range(baseYear, baseYear + numberOfYears).forEach(year -> {

            Grid.Column<?, ?> firstHalfColumn = grid.addColumn(
                    budgetHistory -> budgetHistory.getFirstHalfOfYear(year),
                    new NumberRenderer(dollarFormat))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setId(year + "H1").setCaption("H1");

            Grid.Column<?, ?> secondHalfColumn = grid.addColumn(
                    budgetHistory -> budgetHistory.getSecondHalfOfYear(year),
                    new NumberRenderer(dollarFormat))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setId(year + "H2").setCaption("H2");

            topHeader.join(firstHalfColumn, secondHalfColumn)
                    .setText(year + "");
        });
*/
        // Add a summary footer row to the Grid
        //FooterRow footer = grid.appendFooterRow();
        // Update the summary row every time data has changed
        // by collecting the sum of each column's data
        /*
        grid.getDataProvider().addDataProviderListener(event -> {

            List<Employee> data = event.getSource()
                    .fetch(new Query<>()).collect(Collectors.toList());

            IntStream.range(baseYear, baseYear + numberOfYears)
                    .forEach(year -> {

                        BigDecimal firstHalfSum = data.stream()
                                .map(budgetHistory -> budgetHistory.getFirstHalfOfYear(year))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal secondHalfSum = data.stream()
                                .map(budgetHistory -> budgetHistory.getSecondHalfOfYear(year))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        footer.getCell(year + "H1")
                                .setText(dollarFormat.format(firstHalfSum));
                        footer.getCell(year + "H2")
                                .setText(dollarFormat.format(secondHalfSum));
                    });
        });
        */
        // Fire a data change event to initialize the summary footer
        grid.getDataProvider().refreshAll();

        // Allow column reordering
        grid.setColumnReorderingAllowed(true);

        // Allow column hiding
        grid.getColumns().stream().forEach(column -> column.setHidable(true));

        return grid;
    }

    // This option is toggleable in the settings menu
    /*
    private void setColumnFiltering(boolean filtered) {
        if (filtered && filteringHeader == null) {
            filteringHeader = sample.appendHeaderRow();

            // Add new TextFields to each column which filters the data from
            // that column
            TextField filteringField = getColumnFilterField();
            filteringField.addValueChangeListener(event -> {
                dataProvider.setFilter(CompanyBudgetHistory::getCompany, company -> {
                    if (company == null) {
                        return false;
                    }
                    String companyLower = company.toLowerCase(Locale.ENGLISH);
                    String filterLower = event.getValue().toLowerCase(Locale.ENGLISH);
                    return companyLower.contains(filterLower);
                });
            });
            filteringHeader.getCell("CompanyNameColumn")
                    .setComponent(filteringField);
        } else if (!filtered && filteringHeader != null) {
            dataProvider.clearFilters();
            sample.removeHeaderRow(filteringHeader);
            filteringHeader = null;
        }
    }

    private TextField getColumnFilterField() {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setPlaceholder("Filter");
        return filter;
    }
    */

    private void loadData() {
        createUserInfoCard();
        createUserSalaryCard();
        createRoleCard();
        createUserStatusCard();
        createUserPhotoCard();
    }

    private void createUserSalaryCard() {
        userSalaryCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userSalaryCard);
    }

    private void createUserInfoCard() {
        userInfoCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userInfoCard);
    }

    private void createRoleCard() {
        roleCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(roleCard);
    }

    private void createUserStatusCard() {
        userStatusCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userStatusCard);
    }

    private void createUserPhotoCard() {
        userPhotoCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userPhotoCard);

    }
}

class Employee {
    String name;
    String status;
    int hours;
    int salary;

    public Employee() {
    }

    public Employee(String name, String status, int hours, int salary) {
        this.name = name;
        this.status = status;
        this.hours = hours;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}