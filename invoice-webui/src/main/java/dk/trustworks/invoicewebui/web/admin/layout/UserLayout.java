package dk.trustworks.invoicewebui.web.admin.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.CompanyAggregateData;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.BiService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.admin.components.*;
import dk.trustworks.invoicewebui.web.admin.model.Employee;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardContent;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static dk.trustworks.invoicewebui.utils.DateUtils.getDiffYears;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@SpringUI
@SpringComponent
public class UserLayout {

    @Autowired
    private BiService biService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRestService teamRestService;

    @Autowired
    private UserInfoCardImpl userInfoCard;

    @Autowired
    private UserSalaryCardImpl userSalaryCard;

    @Autowired
    private UserStatusCardImpl userStatusCard;

    @Autowired
    private TeamRolesCardImpl teamRolesCard;

    @Autowired
    private UserPhotoCardImpl userPhotoCard;

    @Autowired
    private RoleCardImpl roleCard;

    private ResponsiveRow employeeContentRow;

    private ListDataProvider<Employee> dataProvider;

    private String selectedUseruuid;

    public void createEmployeeLayout(final ResponsiveRow contentRow) {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        contentRow.addColumn()
                .withComponent(responsiveLayout)
                .withDisplayRules(12, 12, 12, 12);

        ResponsiveRow cardsContentRow = responsiveLayout.addRow();
        ResponsiveRow selectionContentRow = responsiveLayout.addRow();
        employeeContentRow = responsiveLayout.addRow();

        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Consultants", "The car", userService.findCurrentlyEmployedUsers(true, ConsultantType.CONSULTANT).size()+"", "medium-blue")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Staff", "The engine", userService.findCurrentlyEmployedUsers(true, ConsultantType.STAFF).size()+"", "dark-green")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Students", "The transmission", userService.findCurrentlyEmployedUsers(true, ConsultantType.STUDENT).size()+"", "orange")));
        cardsContentRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new TopCardImpl(new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Former", "CO2", userService.findByStatus(StatusType.TERMINATED).size()+"", "dark-grey")));

        ComboBox<User> userComboBox = new ComboBox<>();

        MButton addUserButton = new MButton("add user").withStyleName("flat", "friendly").withListener((Button.ClickListener) event -> {

            final Window window = new MWindow("Window").withCenter().withModal(true);
            window.setWidth(400.0f, Sizeable.Unit.PIXELS);
            window.setHeight(400.0f, Sizeable.Unit.PIXELS);
            final FormLayout content = new FormLayout();

            TextField firstname = new MTextField("First name").withFullWidth();
            TextField lastname = new MTextField("Last name").withFullWidth();
            ComboBox<String> gender = new ComboBox<>("Gender");
            gender.setItems("FEMALE", "MALE");
            gender.setSizeFull();
            TextField username = new MTextField("Username").withAutoCapitalizeOff().withFullWidth();

            Button btnCreate = new MButton("Create", createEvent -> {
                User user = new User();
                user.setUuid(UUID.randomUUID().toString());
                user.setFirstname(firstname.getValue());
                user.setLastname(lastname.getValue());
                user.setUsername(username.getValue());
                user.setEmail(username.getValue()+"@trustworks.dk");
                user.setGender(gender.getValue());

                userService.create(user);
                window.close();
                userComboBox.setItems(userService.findAll(false));
                userComboBox.setSelectedItem(user);
            }).withFullWidth();
            Button btnCancel = new MButton("Cancel", cancel -> window.close()).withFullWidth();

            content.addComponent(firstname);
            content.addComponent(lastname);
            content.addComponent(gender);
            content.addComponent(username);
            content.addComponent(new MVerticalLayout(btnCreate, btnCancel).withFullWidth());

            content.setMargin(true);
            window.setContent(content);

            UI.getCurrent().addWindow(window);
        });

        userComboBox.setItems(userService.findAll(false));
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        selectionContentRow
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(new MHorizontalLayout().withFullWidth().add(userComboBox).add(addUserButton));
        userComboBox.addValueChangeListener(event -> {
            selectedUseruuid = event.getValue().getUuid();
            employeeContentRow.removeAllComponents();
            loadData(employeeContentRow);
        });

        employeeContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new BoxImpl().instance(getGrid()).witHeight(400, PIXELS));
        employeeContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new BoxImpl().instance(getChart()).witHeight(400, PIXELS));
    }

    protected Component getChart() {
        Chart chart = new Chart();
        chart.setSizeFull();
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

        List<CompanyAggregateData> data = biService.getCompanyAggregateDataByPeriod(periodStart, periodEnd);

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            //List<User> usersByLocalDate = userService.findEmployedUsersByDate(currentDate, true, ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT);
            revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")), data.stream().filter(d -> d.getMonth().isEqual(currentDate)).mapToInt(CompanyAggregateData::getNumOfEmployees).sum()));
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

        ArrayList<Employee> employees = new ArrayList<>();
        for (User user : userService.findAll(false)) {
            Optional<Salary> salary = user.getSalaries().stream().max(Comparator.comparing(Salary::getActivefrom));
            if(!salary.isPresent()) continue;
            Optional<UserStatus> userStatus = user.getStatuses().stream().max((Comparator.comparing(UserStatus::getStatusdate)));
            if(!userStatus.isPresent()) continue;
            LocalDate now = LocalDate.now();
            UserContactinfo userContactinfo = userService.findUserContactinfo(user.getUuid());
            Employee employee = new Employee(
                        user.getUuid(),
                        userService.getUserStatus(user, now).getType().name(),
                        teamRestService.findByRoles(user.getUuid(), now, "MEMBER").stream().findFirst().orElse(new Team("", "NONE", "", "", true, true)).getName(),
                        user.getFirstname() + " " + user.getLastname(),
                        user.getCpr(),
                        getDiffYears(user.getBirthday(), LocalDate.now())+"",
                        stringIt(userService.findEmployedDate(user).orElse(LocalDate.now())),
                        userContactinfo.getStreetname() + ", " + userContactinfo.getPostalcode()+ " "+userContactinfo.getCity(), //user.getUserContactinfo().getStreetname(),
                        user.getPhone(),
                        user.getEmail(),
                        userStatus.get().getAllocation()+"",
                        salary.get().getSalary(),
                        salary.get().getSalary()*12,
                        user.isPension()+"",
                        user.isHealthcare()+"",
                        user.isPhotoconsent()+"",
                        user.getPensiondetails(),
                        user.getDefects(),
                        user.getOther(),
                        userStatus.get().getStatus().name()
                    );
            /*,
                    userStatus.get().getStatus().name(),
                    userStatus.get().getAllocation(),
                    salary.get().getSalary());*/
            employees.add(employee);
        }

        dataProvider = new ListDataProvider<>(employees);
        grid.setDataProvider(dataProvider);

        // Set the selection mode
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField filterRole = singleFilterTextField(grid.addColumn(Employee::getRole).setCaption("Role"), filterRow);
        filterRole.addValueChangeListener(event -> dataProvider.setFilter((item) -> item.getRole().toLowerCase().contains(filterRole.getValue().toLowerCase())));

        TextField filterTeam = singleFilterTextField(grid.addColumn(Employee::getTeam).setCaption("Team"), filterRow);
        filterTeam.addValueChangeListener(event -> dataProvider.setFilter((item) -> item.getTeam().toLowerCase().contains(filterTeam.getValue().toLowerCase())));

        TextField filterName = singleFilterTextField(grid.addColumn(Employee::getName).setCaption("Name"), filterRow);
        filterName.addValueChangeListener(event -> dataProvider.setFilter((item) -> item.getName().toLowerCase().contains(filterName.getValue().toLowerCase())));

        grid.addColumn(Employee::getCpr).setCaption("CPR");
        grid.addColumn(Employee::getAge).setCaption("Age");
        grid.addColumn(Employee::getEmployedDate).setCaption("Employed");
        grid.addColumn(Employee::getAdresse).setCaption("Address");
        grid.addColumn(Employee::getPhone).setCaption("Phone");
        grid.addColumn(Employee::getEmail).setCaption("Email");
        grid.addColumn(Employee::getAllocation).setCaption("Allocation");
        grid.addColumn(Employee::getMonthSalary).setCaption("Salary");
        grid.addColumn(Employee::getPension).setCaption("Pension");
        grid.addColumn(Employee::getHealthcare).setCaption("Healthcare");
        grid.addColumn(Employee::getAddedPension).setCaption("Pension details");
        grid.addColumn(Employee::getDefects).setCaption("Defects");
        grid.addColumn(Employee::getPhotoconsent).setCaption("Photo consent");
        grid.addColumn(Employee::getOther).setCaption("Other");

        TextField filterStatus = singleFilterTextField(grid.addColumn(Employee::getStatus).setCaption("Status"), filterRow);
        filterStatus.addValueChangeListener(event -> dataProvider.setFilter((item) -> item.getStatus().toLowerCase().contains(filterStatus.getValue().toLowerCase())));

        grid.getDataProvider().refreshAll();

        grid.setColumnReorderingAllowed(true);

        grid.getColumns().forEach(column -> column.setHidable(true));

        grid.addItemClickListener(event -> {
            if(!event.getMouseEventDetails().isDoubleClick()) return;
            selectedUseruuid = event.getItem().getUuid();
            employeeContentRow.removeAllComponents();
            loadData(employeeContentRow);
        });

        return grid;
    }

    private TextField singleFilterTextField(Grid.Column<Employee, String> column, HeaderRow filterRow){
        TextField filterField = new TextField();
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.setWidth("100%");
        filterRow.getCell(column).setComponent(new MHorizontalLayout(filterField));
        return filterField;
    }

    private void loadData(ResponsiveRow employeeContentRow) {
        createUserInfoCard(employeeContentRow);
        createUserSalaryCard(employeeContentRow);
        createRoleCard(employeeContentRow);
        createUserStatusCard(employeeContentRow);
        createUserPhotoCard(employeeContentRow);
        createTeamRolesCard(employeeContentRow);
    }

    private void createUserSalaryCard(ResponsiveRow employeeContentRow) {
        userSalaryCard.init(selectedUseruuid);
        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userSalaryCard);
    }

    private void createUserInfoCard(ResponsiveRow employeeContentRow) {
        userInfoCard.init(selectedUseruuid);
        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userInfoCard);
    }

    private void createRoleCard(ResponsiveRow employeeContentRow) {
        roleCard.init(selectedUseruuid);

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(roleCard);
    }

    private void createUserStatusCard(ResponsiveRow employeeContentRow) {
        userStatusCard.init(selectedUseruuid);

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(userStatusCard);
    }

    private void createTeamRolesCard(ResponsiveRow employeeContentRow) {
        teamRolesCard.init(selectedUseruuid);

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(teamRolesCard);
    }

    private void createUserPhotoCard(ResponsiveRow employeeContentRow) {
        userPhotoCard.init(selectedUseruuid);

        employeeContentRow
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(userPhotoCard);

    }
}
