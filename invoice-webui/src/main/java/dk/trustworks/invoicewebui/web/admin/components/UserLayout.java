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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardContent;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@SpringUI
@SpringComponent
public class UserLayout {

    @Autowired
    private ConsultantRepository consultantRepository;

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

    ComboBox<User> userComboBox;

    public void createEmployeeLayout(final ResponsiveRow contentRow) {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        contentRow.addColumn()
                .withComponent(responsiveLayout)
                .withDisplayRules(12, 12, 12, 12);

        ResponsiveRow cardsContentRow = responsiveLayout.addRow();
        ResponsiveRow selectionContentRow = responsiveLayout.addRow();
        ResponsiveRow employeeContentRow = responsiveLayout.addRow();



        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Consultants", "The car", consultantRepository.findByTypeAndStatus(ConsultantType.CONSULTANT, StatusType.ACTIVE).size()+"", "medium-blue")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Staff", "The engine", consultantRepository.findByTypeAndStatus(ConsultantType.STAFF, StatusType.ACTIVE).size()+"", "dark-green")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Students", "The transmission", consultantRepository.findByTypeAndStatus(ConsultantType.STUDENT, StatusType.ACTIVE).size()+"", "orange")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Former", "CO2", consultantRepository.findByStatus(StatusType.TERMINATED).size()+"", "dark-grey")));

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
        selectionContentRow
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(new MHorizontalLayout().withFullWidth().add(userComboBox).add(addUserButton));
        userComboBox.addValueChangeListener(event -> {
            employeeContentRow.removeAllComponents();
            loadData(employeeContentRow);
        });

        Card card = new Card();
        card.getContent().addComponent(getGrid());
        employeeContentRow.addColumn().withDisplayRules(12, 12, 8, 8)
                .withComponent(card);

        employeeContentRow.addColumn().withDisplayRules(12, 12, 4, 4)
                .withComponent(getChart());
    }

    protected Component getChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        LocalDate periodStart = LocalDate.of(2014, 2, 1);
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

        grid.getDataProvider().refreshAll();

        grid.setColumnReorderingAllowed(true);

        grid.getColumns().stream().forEach(column -> column.setHidable(true));

        return grid;
    }

    private void loadData(ResponsiveRow employeeContentRow) {
        createUserInfoCard(employeeContentRow);
        createUserSalaryCard(employeeContentRow);
        createRoleCard(employeeContentRow);
        createUserStatusCard(employeeContentRow);
        createUserPhotoCard(employeeContentRow);
    }

    private void createUserSalaryCard(ResponsiveRow employeeContentRow) {
        userSalaryCard.init(userComboBox.getSelectedItem().get().getUuid());
        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userSalaryCard);
    }

    private void createUserInfoCard(ResponsiveRow employeeContentRow) {
        userInfoCard.init(userComboBox.getSelectedItem().get().getUuid());
        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userInfoCard);
    }

    private void createRoleCard(ResponsiveRow employeeContentRow) {
        roleCard.init(userComboBox.getSelectedItem().get().getUuid());

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(roleCard);
    }

    private void createUserStatusCard(ResponsiveRow employeeContentRow) {
        userStatusCard.init(userComboBox.getSelectedItem().get().getUuid());

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userStatusCard);
    }

    private void createUserPhotoCard(ResponsiveRow employeeContentRow) {
        userPhotoCard.init(userComboBox.getSelectedItem().get().getUuid());

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 6)
                .withComponent(userPhotoCard);

    }
}
