package dk.trustworks.invoicewebui.web.stats.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.TeamRole;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.admin.components.*;
import dk.trustworks.invoicewebui.web.admin.layout.DocumentLayout;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.dashboard.cards.DashboardBoxCreator;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.employee.components.cards.EmployeeContactInfoCardController;
import dk.trustworks.invoicewebui.web.stats.components.charts.revenue.AverageRatePerConsultantChart;
import dk.trustworks.invoicewebui.web.stats.components.charts.revenue.RevenuePerConsultantChart;
import dk.trustworks.invoicewebui.web.stats.components.charts.salaries.ConsultantsSalariesChart;
import dk.trustworks.invoicewebui.web.stats.components.charts.size.TeamConsultantCountChart;
import dk.trustworks.invoicewebui.web.vtv.components.HoursPerConsultantChart;
import dk.trustworks.invoicewebui.web.vtv.components.UtilizationPerMonthChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.model.enums.TeamMemberType.LEADER;
import static dk.trustworks.invoicewebui.model.enums.TeamMemberType.SPONSOR;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;


/**
 * Created by hans on 20/09/2017.
 */
@SpringComponent
@SpringUI
public class TeamStatsLayout extends VerticalLayout {

    @Autowired
    private BiService biService;

    @Autowired
    private RevenueService revenueService;

    @Autowired
    private TopGrossingConsultantsChart topGrossingConsultantsChart;

    @Autowired
    private RevenuePerConsultantChart revenuePerConsultantChart;

    @Autowired
    private AverageConsultantRevenueByYearChart averageConsultantRevenueByYearChart;

    @Autowired
    private TeamConsultantCountChart teamConsultantCountChart;

    @Autowired
    private ConsultantsSalariesChart consultantsSalariesChart;

    @Autowired
    private UtilizationPerMonthChart utilizationPerMonthChart;

    @Autowired
    private AverageRatePerConsultantChart averageRatePerConsultantChart;

    @Autowired
    private HoursPerConsultantChart hoursPerConsultantChart;

    @Autowired
    private PurposeBox purposeBox;

    @Autowired
    private NotesBox notesBox;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRestService teamService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private DashboardBoxCreator dashboardBoxCreator;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private EmployeeContactInfoCardController employeeContactInfoCardController;

    @Autowired
    private UserSalaryCardImpl userSalaryCard;

    @Autowired
    private UserStatusCardImpl userStatusCard;

    @Autowired
    private UserPhotoCardImpl userPhotoCard;

    @Autowired
    private DocumentLayout documentLayout;

    private Team team = null;

    private ResponsiveRow teamListContentRow;
    private ResponsiveRow baseContentRow;

    private ResponsiveRow buttonContentRow;

    private ResponsiveRow teamContentRow;
    private ResponsiveRow consultantsContentRow;
    private ResponsiveRow teamleadContentRow;
    private ResponsiveRow trustworksContentRow;
    private ResponsiveRow administrationContentRow;
    private ResponsiveRow individualsContentRow;

    public TeamStatsLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        teamListContentRow = responsiveLayout.addRow();
        baseContentRow = responsiveLayout.addRow();
        buttonContentRow = responsiveLayout.addRow();
        teamContentRow = responsiveLayout.addRow();
        consultantsContentRow = responsiveLayout.addRow();
        consultantsContentRow.setVisible(false);
        teamleadContentRow = responsiveLayout.addRow();
        teamleadContentRow.setVisible(false);
        trustworksContentRow = responsiveLayout.addRow();
        trustworksContentRow.setVisible(false);
        administrationContentRow = responsiveLayout.addRow();
        administrationContentRow.setVisible(false);
        individualsContentRow = responsiveLayout.addRow();
        individualsContentRow.setVisible(false);
        addComponent(responsiveLayout);
        loadData();
        createDataDFoundation();
        return this;
    }

    private void createDataDFoundation() {
        List<User> users = teamService.getUniqueUsersFromTeamsByFiscalYear(2021, "48b5c8d0-a56b-45b8-92db-ba1e09fd8222");
        List<EmployeeAggregateData> data = biService.getEmployeeAggregateDataByPeriod(LocalDate.of(2021, 7, 1), LocalDate.of(2022, 7, 1));
        //List<Work> workList = workService.findByPeriod(LocalDate.of(2021, 7, 1), LocalDate.of(2022, 7, 1));

        for (User user : users) {
            List<Double> work = new ArrayList<>();
            List<Double> vacation = new ArrayList<>();
            List<Double> sickness = new ArrayList<>();
            List<Double> other = new ArrayList<>();
            List<Double> workableHours = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                LocalDate date = LocalDate.of(2021, 7, 1).plusMonths(i);
                Optional<EmployeeAggregateData> first = data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(date)).findFirst();
                if(!first.isPresent()) {
                    work.add(0.0);
                    vacation.add(0.0);
                    sickness.add(0.0);
                    other.add(0.0);
                    workableHours.add(0.0);
                    continue;
                }
                work.add(first.get().getRegisteredHours());
                vacation.add(first.get().getVacation());
                sickness.add(first.get().getSickdays());
                other.add(first.get().getHelpedColleagueHours());
                workableHours.add(first.get().getGrossAvailableHours());
            }
            System.out.print(user.getUsername());
            for (int i = 0; i < 12; i++) {
                LocalDate date = LocalDate.of(2021, 7, 1).plusMonths(i);
                System.out.print(stringIt(date));
                if(i<11) System.out.print(",");
            }
            System.out.println();
            System.out.print("work,");
            System.out.println(work.stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.print("vacation,");
            System.out.println(vacation.stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.print("sickness,");
            System.out.println(sickness.stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.print("other,");
            System.out.println(other.stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.print("work hours,");
            System.out.println(workableHours.stream().map(Object::toString).collect(Collectors.joining(",")));
        }
        System.out.println("\n-------\n");
    }

    private void loadData() {
        createMonthReportCard();
    }

    private void createMonthReportCard() {
        User teamlead = userService.getLoggedInUser().orElseThrow(() -> new RuntimeException("No user logged in"));

        List<Team> teams = teamService.findByRoles(teamlead.getUuid(), LocalDate.now(), LEADER.name(), SPONSOR.name());
        if(teams.size() == 0) {
            baseContentRow.addColumn()
                    .withDisplayRules(12, 12, 12, 12)
                    .withComponent(new Label("No Teams!"), ResponsiveColumn.ColumnComponentAlignment.CENTER);
            return;
        }

        List<Image> btnTeams = new ArrayList<>();
        teams.forEach(t -> {
            //final Button btn = new MButton(photoService.getRelatedPhotoResource(t.getLogouuid()), t.getName(), event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");
            final Image btnImage = new Image(t.getName(), photoService.getRelatedPhotoResource(t.getUuid()));
            btnImage.setHeight(125, Unit.PIXELS);
            btnImage.setWidth(100, Unit.PERCENTAGE);
            //btnImage

            btnTeams.add(btnImage);
            teamListContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnImage);
            btnImage.addClickListener(event -> {
                btnTeams.forEach(button -> button.setEnabled(true));
                btnImage.setEnabled(false);
                team = t;
                baseContentRow.removeAllComponents();
                buttonContentRow.removeAllComponents();
                teamContentRow.removeAllComponents();
                consultantsContentRow.removeAllComponents();
                teamleadContentRow.removeAllComponents();
                trustworksContentRow.removeAllComponents();
                administrationContentRow.removeAllComponents();
                individualsContentRow.removeAllComponents();
                hideAllDynamicRows();
                addBaseRowComponents();
                //addTeamCharts();
                addAdminPage();
                administrationContentRow.setVisible(true);
            });
        });

        team = teams.get(0);
        btnTeams.get(0).setEnabled(false);

        addBaseRowComponents();
        administrationContentRow.setVisible(true);
        addAdminPage();
    }

    private void addBaseRowComponents() {
        System.out.println("--- Start Dashboard Boxes ---");

        long l = System.currentTimeMillis();

        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodTeamPeopleBox(team.getUuid())));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getTotalTeamProfits(team)));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createBillableHoursBox()));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getTeamAllocationBox(team)));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));

        System.out.println("--- Done Dashboard Boxes ---");

        final Button btnTeam = new MButton(MaterialIcons.NATURE_PEOPLE, "Team", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        final Button btnConsultants = new MButton(MaterialIcons.PEOPLE, "Consultants", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats that show yearly progress
        final Button btnTeamlead = new MButton(MaterialIcons.PERSON,"Teamlead", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats that show customer distributions
        final Button btnTrustworks = new MButton(MaterialIcons.BUSINESS,"Trustworks", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats for administration, like salary, vacation, illness
        final Button btnAdministration = new MButton(MaterialIcons.INBOX, "Administration", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        // Stats for individuals
        final Button btnIndividuals = new MButton( MaterialIcons.FACE, "Individuals", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");


        btnAdministration.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, administrationContentRow);
            addAdminPage();
        });
        btnTeam.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, teamContentRow);
            addTeamCharts();
        });
        btnConsultants.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, consultantsContentRow);
            addConsultantCharts();
        });
        btnTeamlead.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, teamleadContentRow);
            addTeamleadCharts();
        });
        btnTrustworks.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, trustworksContentRow);
            addTrustworksCharts();
        });
        btnIndividuals.addClickListener(event -> {
            setNewButtonPressState(btnTeam, btnConsultants, btnTeamlead, btnTrustworks, btnAdministration, btnIndividuals, event, individualsContentRow);
            addIndividualCharts();
        });

        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnAdministration);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnTeam);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnConsultants);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnTeamlead);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnTrustworks);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnIndividuals);
    }

    public void addTeamCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        teamContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = createDateSelectorHeader(searchRow, chartRow);

        createTeamCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
    }

    private AtomicReference<LocalDate> createDateSelectorHeader(ResponsiveRow searchRow, ResponsiveRow chartRow) {
        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createTeamCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createTeamCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);
        return currentFiscalYear;
    }

    private void createIndividualSearchHeader(ResponsiveRow searchRow, ResponsiveRow chartRow) {
        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());
        AtomicReference<Image> selectedEmployeeImage = new AtomicReference<>(null);
        AtomicReference<User> selectedEmployee = new AtomicReference<>(null);

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createIndividualCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), selectedEmployee.get());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createIndividualCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), selectedEmployee.get());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));

        //String teamuuid = teamService.getTeamuuidsAsLeader(userService.getLoggedInUser().get()); //userService.getLoggedInUser().get().getTeamuuid();

        for (User employee : teamService.getUsersByTeamByMonth(team.getUuid(), LocalDate.now().withDayOfMonth(1))) {
            Image memberImage = photoService.getRoundMemberImage(employee.getUuid(), false, 60, Unit.PERCENTAGE);
            memberImage.addClickListener(event -> {
                chartRow.removeAllComponents();
                if(selectedEmployeeImage.get() != null) {
                    selectedEmployeeImage.get().removeStyleName("img-circle-gold");
                    selectedEmployeeImage.get().addStyleName("img-circle");
                }
                selectedEmployeeImage.set(memberImage);
                selectedEmployeeImage.get().removeStyleName("img-circle");
                selectedEmployeeImage.get().addStyleName("img-circle-gold");

                selectedEmployee.set(employee);
                createIndividualCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), employee);
            });
            searchRow.addColumn().withDisplayRules(3, 2, 1, 1).withComponent(memberImage);
        }

    }

    private void createAdministrationSearchHeader(ResponsiveRow searchRow, ResponsiveRow chartRow) {
        AtomicReference<Image> selectedEmployeeImage = new AtomicReference<>(null);
        //AtomicReference<User> selectedEmployee = new AtomicReference<>(null);

        for (User employee : teamService.getUsersByTeamByMonth(team.getUuid(), LocalDate.now().withDayOfMonth(1))) {
            Image memberImage = photoService.getRoundMemberImage(employee.getUuid(), false, 60, Unit.PERCENTAGE);
            memberImage.addClickListener(event -> {
                chartRow.removeAllComponents();
                if(selectedEmployeeImage.get() != null) {
                    selectedEmployeeImage.get().removeStyleName("img-circle-gold");
                    selectedEmployeeImage.get().addStyleName("img-circle");
                }
                selectedEmployeeImage.set(memberImage);
                selectedEmployeeImage.get().removeStyleName("img-circle");
                selectedEmployeeImage.get().addStyleName("img-circle-gold");

                //selectedEmployee.set(employee);
                createAdminPage(chartRow, employee);
            });
            searchRow.addColumn().withComponent(new Label());
            searchRow.addColumn().withDisplayRules(3, 2, 1, 1).withComponent(memberImage);
            searchRow.addColumn().withComponent(new Label());
        }
    }

    private void createTeamCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd) {
        System.out.println("--- Start company charts ---");
        long l = System.currentTimeMillis();

        Box teamConsultantsPerMonthCard = new Box();
        teamConsultantsPerMonthCard.getContent().addComponent(teamConsultantCountChart.createTeamConsultantCountChart(localDateStart.getYear(), team.getUuid()));

        Box utilizationPerMonthCard = new Box();
        utilizationPerMonthCard.getContent().addComponent(utilizationPerMonthChart.createGroupUtilizationPerMonthChart(localDateStart, localDateEnd, team.getUuid()));

        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));

        System.out.println("--- Done company charts ---");

        // Weighted Average Margin Per Month

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(utilizationPerMonthCard);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(teamConsultantsPerMonthCard);


    }

    public void addConsultantCharts() {
        if(consultantsContentRow.getComponentCount()>0) return;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        consultantsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);

        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
    }

    private void createConsultantCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd) {
        System.out.println("--- Start consultant charts ---");
        long l = System.currentTimeMillis();

        Box hoursPerConsultantBox = new Box();
        hoursPerConsultantBox.getContent().addComponent(hoursPerConsultantChart.createHoursPerConsultantChart(LocalDate.now().withDayOfMonth(1), teamService.getUniqueUsersFromTeamsByMonth(LocalDate.now().withDayOfMonth(1), team.getUuid()), true));

        Box topGrossingConsultantsBox = new Box();
        topGrossingConsultantsBox.getContent().addComponent(topGrossingConsultantsChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd, team.getUuid()));//teamService.getTeamuuidsAsLeader(userService.getLoggedInUser().get())));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));

        Box consultantsSalaryBox = new Box();
        consultantsSalaryBox.getContent().addComponent(consultantsSalariesChart.createConsultantsSalariesChart(localDateStart, localDateEnd, team.getUuid()));//teamService.getTeamuuidsAsLeader(userService.getLoggedInUser().get())));
        System.out.println("l - System.currentTimeMillis() = " + (l - System.currentTimeMillis()));

        Box averageRatePerConsultantCard = new Box();
        averageRatePerConsultantCard.getContent().addComponent(averageRatePerConsultantChart.createAverageRatePerConsultantChart(localDateStart.getYear(), team.getUuid()));


        System.out.println("--- Done consultant charts ---");

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(hoursPerConsultantBox);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(topGrossingConsultantsBox);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsSalaryBox);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(averageRatePerConsultantCard);

    }

    public void addTeamleadCharts() {
        if(teamleadContentRow.getComponentCount()>0) return;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        teamleadContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);

        ResponsiveRow searchRow = responsiveLayout.addRow();
        ResponsiveRow teamleadPortraitsRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            //createTeamleadCharts(chartRow, currentFiscalYear.get().getYear());
            getTeamleadsInFiscalYear(teamleadPortraitsRow, chartRow, currentFiscalYear.get().getYear());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            //createTeamleadCharts(chartRow, currentFiscalYear.get().getYear());
            getTeamleadsInFiscalYear(teamleadPortraitsRow, chartRow, currentFiscalYear.get().getYear());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        getTeamleadsInFiscalYear(teamleadPortraitsRow, chartRow, currentFiscalYear.get().getYear());

        //createTeamleadCharts(chartRow, currentFiscalYear.get().getYear());
    }

    private void getTeamleadsInFiscalYear(ResponsiveRow teamleadPortraitsRow, ResponsiveRow chartRow, int fiscalYear) {
        teamleadPortraitsRow.removeAllComponents();
        LocalDate startOfFiscalYear = LocalDate.of(fiscalYear, 7, 1);
        Set<User> teamleads = new HashSet<>();
        for (int monthCount = 0; monthCount < 12; monthCount++) {
            LocalDate currentDate = startOfFiscalYear.plusMonths(monthCount);
            teamleads.addAll(teamService.findTeamleadersByMonth(team.getUuid(), currentDate));
        }
        for (User teamlead : teamleads) {
            Image memberImage = photoService.getRoundMemberImage(teamlead.getUuid(), false, 100, Unit.PERCENTAGE);
            memberImage.addClickListener(event -> {
                LocalDate endDate = startOfFiscalYear.plusYears(1);
                Optional<TeamRole> teamRole = teamService.findUserTeamRoles(teamlead.getUuid()).stream().filter(t -> t.getTeammembertype().equals(LEADER) && DateUtils.isOverlapping(t.getStartdate(), t.getEnddate()==null?startOfFiscalYear.plusYears(1):t.getEnddate(), startOfFiscalYear, endDate)).findFirst();
                teamRole.ifPresent(role -> System.out.println("teamRole.get() = " + role));
                createTeamleadCharts(chartRow, teamlead, (teamRole.isPresent()&&teamRole.get().getStartdate().isAfter(startOfFiscalYear))?teamRole.get().getStartdate(): startOfFiscalYear, (teamRole.isPresent()&&teamRole.get().getEnddate()!=null&&teamRole.get().getEnddate().isBefore(endDate))?teamRole.get().getEnddate():endDate);
            });
            teamleadPortraitsRow.addColumn().withDisplayRules(3, 2, 1, 1).withComponent(memberImage);
        }
    }

    public void addTrustworksCharts() {
        if(trustworksContentRow.getComponentCount()>0) return;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        teamleadContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);

        final ResponsiveRow chartRow = responsiveLayout.addRow();

        createTrustworksCharts(chartRow);
    }


    public void addIndividualCharts() {
        if(individualsContentRow.getComponentCount()>0) return;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        individualsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        createIndividualSearchHeader(searchRow, chartRow);
    }

    public void addAdminPage() {
        if(administrationContentRow.getComponentCount()>0) return;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        administrationContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow adminRow = responsiveLayout.addRow();

        createAdministrationSearchHeader(searchRow, adminRow);
    }

    private void createTrustworksCharts(ResponsiveRow chartRow) {
        Box averageConsultantRevenueByYearCard = new Box();
        averageConsultantRevenueByYearCard.getContent().addComponent(averageConsultantRevenueByYearChart.createRevenuePerConsultantChart());

        //chartRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(null);
    }

    private void createTeamleadCharts(ResponsiveRow chartRow, User teamlead, LocalDate startDate, LocalDate endDate) {
        System.out.println("TeamStatsLayout.createTeamleadCharts");
        System.out.println("chartRow = " + chartRow + ", teamlead = " + teamlead + ", startDate = " + startDate + ", endDate = " + endDate);
        chartRow.removeAllComponents();
        Box revenuePerConsultantCard = new Box();
        revenuePerConsultantCard.getContent().addComponent(revenuePerConsultantChart.createRevenuePerConsultantChart(teamlead, startDate, endDate));

        /*
        private int teamUtil;
    private int teamSize;
    private int teamSizeFactor;
    private int adjustedTeamUtil;
    private double teamBonus;
    private double production;
    private double productionBonus;
         */

        Box teamBonusBox = new Box();
        FormLayout teamBonusForm = new FormLayout();
        teamBonusBox.getContent().addComponent(teamBonusForm);
        teamBonusForm.setMargin(true);
        teamBonusForm.addStyleName("outlined");
        teamBonusForm.setSizeFull();
        teamBonusForm.addComponent(new MTextField("Calculated period", stringIt(startDate)+" - "+stringIt(endDate)).withReadOnly(true));
        double totalTeamRevenue = revenueService.getTotalTeamProfitsByPeriod(startDate, endDate, teamService.getAllTeams().stream().filter(Team::isTeamleadbonus).collect(Collectors.toList())).getValue();
        teamBonusForm.addComponent(new MTextField("Profits across all teams", NumberConverter.formatCurrency(totalTeamRevenue)).withReadOnly(true));
        double averageTeamAllocationByPeriod = statisticsService.getAverageTeamAllocationByPeriod(startDate, endDate, team);
        teamBonusForm.addComponent(new MTextField("Your team utilization", NumberConverter.formatPercentage(averageTeamAllocationByPeriod)).withReadOnly(true));
        float avgGoodPeopleByPeriod = teamService.getAvgGoodPeopleByPeriod(team.getUuid(), startDate, endDate);
        teamBonusForm.addComponent(new MTextField("Your team avg size", NumberConverter.formatDouble(avgGoodPeopleByPeriod)).withReadOnly(true));

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(revenuePerConsultantCard);

        chartRow.addColumn()
                .withDisplayRules(12,12,6,6)
                .withComponent(teamBonusBox);
    }

    private void createIndividualCharts(ResponsiveRow chartRow, LocalDate datefrom, LocalDate dateto, User employee) {
        System.out.println("TrustworksStatsLayout.createIndividualCharts");
        System.out.println("chartRow = " + chartRow + ", employee = " + employee);

        chartRow.removeAllComponents();

        Box consultantUtilizationBox = new Box();
        consultantUtilizationBox.getContent().addComponent(utilizationPerMonthChart.createConsultantUtilizationPerMonthChart(datefrom, dateto, employee));

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(consultantUtilizationBox);
    }



    private void createAdminPage(ResponsiveRow adminPageRow, User employee) {
        System.out.println("TrustworksStatsLayout.createIndividualCharts");
        System.out.println("chartRow = " + adminPageRow + ", employee = " + employee);

        adminPageRow.removeAllComponents();

        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName("flat");

        // *** Create user admin layout
        ResponsiveLayout userAdminLayout = new ResponsiveLayout();
        ResponsiveRow userAdminTabSheetRow = userAdminLayout.addRow();
        userAdminTabSheetRow.addColumn()
                .withDisplayRules(12,12, 4, 4)
                .withComponent(employeeContactInfoCardController.getCard(employee));

        userSalaryCard.init(employee.getUuid());
        userAdminTabSheetRow.addColumn()
                .withDisplayRules(12,12, 4, 4)
                .withComponent(userSalaryCard);

        userPhotoCard.init(employee.getUuid());

        userAdminTabSheetRow
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(userPhotoCard);

        userStatusCard.init(employee.getUuid());
        userAdminTabSheetRow
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(userStatusCard);

        // *** Create kpcLayout
        ResponsiveLayout kpcLayout = new ResponsiveLayout();
        ResponsiveRow kpcTabSheetRow = kpcLayout.addRow();

        kpcTabSheetRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(purposeBox.createPurposeBox(employee));

        kpcTabSheetRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(notesBox.createNotesBox(employee));

        // *** Create employee contract view
        ResponsiveLayout documentsLayout = new ResponsiveLayout();
        ResponsiveRow documentsTabSheetRow = documentsLayout.addRow();

        documentsTabSheetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(documentLayout.init(teamService.getUsersByTeamByMonth(team.getUuid(), LocalDate.now())));

        // *** Add layouts to tabsheet
        tabSheet.addTab(userAdminLayout, "Employee data");
        tabSheet.addTab(kpcLayout, "KPC");
        tabSheet.addTab(documentsLayout, "Contracts");

        adminPageRow.addColumn()
                .withDisplayRules(12,12,12,12)
                .withComponent(tabSheet);
    }

    private String createFiscalYearText(AtomicReference<LocalDate> currentFiscalYear) {
        return "Fiscal year " + currentFiscalYear.get().getYear() + "/" + currentFiscalYear.get().plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }

    private void setNewButtonPressState(Button btnCompany, Button btnTeam, Button btnHistory, Button btnCustomers, Button btnAdministration, Button btnIndividuals, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button... btn) {//, Button btnTeam, Button btnHistory, Button btnCustomers, Button btnAdministration, Button btnIndividuals) {
        for (Button button : btn) {
            button.setEnabled(true);
        }

        /*
        btnCompany.setEnabled(true);
        btnTeam.setEnabled(true);
        btnHistory.setEnabled(true);
        btnCustomers.setEnabled(true);
        btnAdministration.setEnabled(true);
        btnIndividuals.setEnabled(true);
         */
    }

    private void hideAllDynamicRows() {
        teamContentRow.setVisible(false);
        consultantsContentRow.setVisible(false);
        teamleadContentRow.setVisible(false);
        trustworksContentRow.setVisible(false);
        administrationContentRow.setVisible(false);
        individualsContentRow.setVisible(false);
    }

}
