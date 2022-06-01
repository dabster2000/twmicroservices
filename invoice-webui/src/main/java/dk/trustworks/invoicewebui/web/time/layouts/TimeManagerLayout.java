package dk.trustworks.invoicewebui.web.time.layouts;


import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.network.rest.CultureRestService;
import dk.trustworks.invoicewebui.network.rest.WeekRestService;
import dk.trustworks.invoicewebui.repositories.ReceiptsRepository;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import dk.trustworks.invoicewebui.utils.StringUtils;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardContent;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.time.LessonFramedView;
import dk.trustworks.invoicewebui.web.time.components.*;
import dk.trustworks.invoicewebui.web.time.model.WeekItem;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static dk.trustworks.invoicewebui.utils.DateUtils.lastDayOfMonth;
import static dk.trustworks.invoicewebui.utils.StringUtils.distinctByKey;

@SpringComponent
@SpringUI
public class TimeManagerLayout extends ResponsiveLayout {

    private static final Logger log = LoggerFactory.getLogger(TimeManagerLayout.class);

    private final WeekRestService weekRestService;

    private final ProjectService projectService;

    private final UserService userService;

    private final BudgetService budgetService;

    private final ClientService clientService;

    private final WorkService workService;

    private final PhotoService photoService;

    private final ContractService contractService;

    private final ReceiptsRepository receiptsRepository;

    private final CultureRestService cultureRestService;

    private final ResponsiveLayout responsiveLayout;

    private LocalDate currentDate = LocalDate.now().with(DayOfWeek.MONDAY);

    private final FooterButtons footerButtons;
    private final DateButtons dateButtons;
    private final CustomerExpenses customerExpenses;

    private final NumberFormat nf = NumberFormat.getInstance();
    private final Binder<WeekValues> weekValuesBinder = new Binder<>();
    private final Binder<Receipt> receiptBinder = new Binder<>();
    private WeekValues weekDaySums;
    private double sumHours = 0.0;

    private final List<TaskTitle> weekRowTaskTitles = new ArrayList<>();

    @Autowired
    public TimeManagerLayout(ProjectService projectService, UserService userService, ClientService clientService, WorkService workService, TimeService timeService, WeekRestService weekRestService, BudgetService budgetService, ContractService contractService, PhotoService photoService, ReceiptsRepository receiptsRepository, CultureRestService cultureRestService) {
        this.projectService = projectService;
        this.userService = userService;
        this.clientService = clientService;
        this.workService = workService;
        this.weekRestService = weekRestService;
        this.budgetService = budgetService;
        this.photoService = photoService;
        this.contractService = contractService;
        this.receiptsRepository = receiptsRepository;
        this.cultureRestService = cultureRestService;

        footerButtons = new FooterButtons();
        dateButtons = new DateButtons();
        customerExpenses = new CustomerExpenses();
        responsiveLayout = new ResponsiveLayout(ContainerType.FLUID);

        dateButtons.getBtnWeekNumberDecr().addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            log.info("currentDate.minusWeeks(1) = " + currentDate);
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
        });

        dateButtons.getBtnWeekNumberIncr().addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            log.info("currentDate.plusWeeks(1) = " + currentDate);
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
        });

        dateButtons.getSelActiveUser().addValueChangeListener(event -> loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get()));

        footerButtons.getBtnCopyWeek().setIcon(MaterialIcons.CONTENT_COPY);
        footerButtons.getBtnCopyWeek().addClickListener(event1 -> {
            timeService.cloneTaskToWeek(currentDate, dateButtons.getSelActiveUser().getSelectedItem().get());
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
        });

        footerButtons.getBtnEdit().setIcon(MaterialIcons.DELETE);
        footerButtons.getBtnEdit().addClickListener(event -> {
            for (TaskTitle weekRowTaskTitle : weekRowTaskTitles) {
                weekRowTaskTitle.getImgLogo().setVisible(!weekRowTaskTitle.getImgLogo().isVisible());
                weekRowTaskTitle.getBtnDelete().setVisible(!weekRowTaskTitle.getBtnDelete().isVisible());
            }
        });

        footerButtons.getBtnAddTask().setIcon(MaterialIcons.PLAYLIST_ADD);
        footerButtons.getBtnAddTask().addClickListener((Button.ClickEvent event) -> {
            log.info("getBtnAddTask()");
            log.info("weekyear: " + currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            log.info("getWeekyear: " + currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()));
            final Window window = new Window("Add Task");
            window.setWidth(300.0f, PIXELS);
            window.setHeight(450.0f, PIXELS);
            window.setModal(true);

            OnOffSwitch onOffSwitch = new OnOffSwitch(false);
            onOffSwitch.setCaption("Help colleague?");

            ComboBox<User> userComboBox = new ComboBox<>();
            userComboBox.setVisible(false);
            userComboBox.setItemCaptionGenerator(User::getUsername);
            userComboBox.setWidth("100%");
            userComboBox.addStyleName("floating");
            userComboBox.setEmptySelectionAllowed(false);
            userComboBox.setEmptySelectionCaption("Select colleague...");
            List<User> users = userService.findCurrentlyEmployedUsersInclExternalConsultants(true);
            userComboBox.setItems(users);
            UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);

            MLabel spacer = new MLabel("").withWidth(100, PERCENTAGE);

            List<Contract> activeConsultantContracts = getMainContracts(contractService, dateButtons.getSelActiveUser().getSelectedItem().get());
            List<Client> clientResources = getClients(activeConsultantContracts);

            ComboBox<Client> clientComboBox = new ComboBox<>();
            clientComboBox.setItemCaptionGenerator(Client::getName);
            clientComboBox.setWidth("100%");
            clientComboBox.addStyleName("floating");
            clientComboBox.setEmptySelectionAllowed(false);
            clientComboBox.setEmptySelectionCaption("Select client...");
            List<Client> clients = new ArrayList<>(clientResources);
            clientComboBox.setItems(clients);
            if(clients.size()==0) clientComboBox.setEmptySelectionCaption("No active contracts...");

            ComboBox<Project> projectComboBox = new ComboBox<>();
            projectComboBox.setItemCaptionGenerator(Project::getName);
            projectComboBox.setWidth("100%");
            projectComboBox.addStyleName("floating");
            projectComboBox.setEmptySelectionAllowed(false);
            projectComboBox.setEmptySelectionCaption("Select project...");
            projectComboBox.setVisible(false);

            ComboBox<Task> taskComboBox = new ComboBox<>();
            taskComboBox.setItemCaptionGenerator(Task::getName);
            taskComboBox.setWidth("100%");
            taskComboBox.addStyleName("floating");
            taskComboBox.setEmptySelectionAllowed(false);
            taskComboBox.setEmptySelectionCaption("Select task...");
            taskComboBox.setVisible(false);

            Button addTaskButton = new Button("add task");
            addTaskButton.addStyleName("flat friendly");
            addTaskButton.setEnabled(false);

            onOffSwitch.addValueChangeListener(event1 -> {
                if(event1.getValue()) {
                    userComboBox.setVisible(true);
                    userComboBox.setSelectedItem(null);
                    clientComboBox.setVisible(false);
                    projectComboBox.setVisible(false);
                    taskComboBox.setVisible(false);
                } else {
                    userComboBox.setVisible(false);
                    for (User user : users) {
                        if(user.getUuid().equals(userSession.getUser().getUuid())) userComboBox.setSelectedItem(user);
                    }
                }
            });

            userComboBox.addValueChangeListener(event1 -> {
                clientComboBox.setVisible(false);
                if(event1.getValue()==null) return;
                clientComboBox.setVisible(true);
                projectComboBox.setVisible(false);
                taskComboBox.setVisible(false);
                addTaskButton.setEnabled(false);

                List<Contract> newActiveConsultantContracts = getMainContracts(contractService, event1.getValue());
                List<Client> newClientResources = getClients(newActiveConsultantContracts);

                clientComboBox.clear();
                clientComboBox.setItems(newClientResources);
                clientComboBox.setVisible(true);
            });

            clientComboBox.addValueChangeListener(event1 -> {
                taskComboBox.setVisible(false);
                addTaskButton.setEnabled(false);
                projectComboBox.setVisible(false);
                if(event1.getValue()==null) return;

                User user;
                if(onOffSwitch.getValue()){
                    user = userComboBox.getSelectedItem().get();
                } else {
                    user = dateButtons.getSelActiveUser().getSelectedItem().get();//userSession.getUser();
                }
                List<Contract> newActiveConsultantContracts = getMainContracts(contractService, user);
                List<Project> allProjects = projectService.findByClientAndActiveTrueOrderByNameAsc(event1.getValue().getUuid());
                Set<Project> projects;
                // If Trustworks is Client
                if(event1.getValue().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) {
                    projects = new HashSet<>(allProjects);
                } else {
                    projects = newActiveConsultantContracts.stream().map(contract -> contractService.findProjectsByContractuuid(contract.getUuid())).flatMap(List::stream).distinct().filter(allProjects::contains).collect(Collectors.toSet());
                    projects.forEach(System.out::println);
                }

                projectComboBox.clear();
                projectComboBox.setItems(projects);
                projectComboBox.setVisible(true);
            });

            projectComboBox.addValueChangeListener(event1 -> {
                projectComboBox.setVisible(false);
                if(event1.getValue()==null) return;
                projectComboBox.setVisible(true);
                addTaskButton.setEnabled(false);
                taskComboBox.clear();
                if(event1.getValue()==null) return;
                List<Task> tasks = new ArrayList<>(projectService.findOne(event1.getValue().getUuid()).getTasks());
                taskComboBox.setItems(tasks);
                taskComboBox.setVisible(true);
            });

            taskComboBox.addValueChangeListener(event1 -> {
                addTaskButton.setEnabled(false);
                if(event1.getValue()==null) return;
                addTaskButton.setEnabled(true);
            });

            addTaskButton.addClickListener(event1 -> {
                if(onOffSwitch.getValue()) {
                    weekRestService.save(new Week(UUID.randomUUID().toString(),
                            currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()),
                            currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()),
                            dateButtons.getSelActiveUser().getValue().getUuid(),
                            taskComboBox.getSelectedItem().orElse(new Task()).getUuid(), userComboBox.getSelectedItem().orElse(new User()).getUuid()));
                } else {
                    weekRestService.save(new Week(UUID.randomUUID().toString(),
                            currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()),
                            currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()),
                            dateButtons.getSelActiveUser().getValue().getUuid(),
                            taskComboBox.getSelectedItem().orElse(new Task()).getUuid()));
                }
                window.close();
                loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
            });

            window.setContent(new VerticalLayout(onOffSwitch, userComboBox, spacer, clientComboBox, projectComboBox, taskComboBox, addTaskButton));
            this.getUI().addWindow(window);
        });
    }

    private List<Client> getClients(List<Contract> activeConsultantContracts) {
        List<Client> clientList = activeConsultantContracts.stream().filter(StringUtils.distinctByKey(Contract::getClientuuid)).map(contract -> clientService.findOne(contract.getClientuuid())).sorted(Comparator.comparing(Client::getName)).collect(Collectors.toList());
        clientList.add(clientService.findOne("40c93307-1dfa-405a-8211-37cbda75318b"));
        return clientList;
    }

    private List<Contract> getMainContracts(ContractService contractService, User user) {
        LocalDate contractDate = currentDate.with(DayOfWeek.MONDAY);
        List<Contract> timeActiveConsultantContracts = new ArrayList<>(contractService.findTimeActiveConsultantContracts(user, contractDate.withDayOfMonth(1)));
        if(contractDate.getMonthValue()!=currentDate.with(DayOfWeek.SUNDAY).getMonthValue()) {
            // If week spans two months
            timeActiveConsultantContracts.addAll(contractService.findTimeActiveConsultantContracts(user, contractDate.plusMonths(1).withDayOfMonth(1)));
        }
        // Remove duplicates
        return timeActiveConsultantContracts.stream().filter(distinctByKey(Contract::getUuid)).collect(Collectors.toList());
    }

    public ResponsiveLayout init() {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);

        if(userSession == null) return new ResponsiveLayout();

        List<User> users;
        if(UserService.get().isExternal(userSession.getUser())) {
            users = Collections.singletonList(userService.findByUUID(userSession.getUser().getUuid(), false));
        } else {
            users = userService.findCurrentlyEmployedUsersInclExternalConsultants(true);
        }
        dateButtons.getSelActiveUser().setItemCaptionGenerator(User::getUsername);
        System.out.println("users.size() = " + users.size());
        System.out.println("userSession.getUser() = " + userSession.getUser());
        System.out.println("UserService.get().isExternal(userSession.getUser()) = " + UserService.get().isExternal(userSession.getUser()));

        dateButtons.getSelActiveUser().setItems(users);

        User user = null;
        for (User u : users) {
            if (u.getUuid().equals(userSession.getUser().getUuid())) {
                dateButtons.getSelActiveUser().setSelectedItem(u);
                user = u;
            }
        }
        System.out.println("user = " + user);
        //user = dateButtons.getSelActiveUser().getSelectedItem().get();

        loadTimeview(user);

        this.addRow().addColumn()
                .withDisplayRules(12, 12, 10, 10)
                .withOffset(DisplaySize.LG, 1)
                .withOffset(DisplaySize.MD, 1)
                .withComponent(responsiveLayout);
        return this;
    }

    private void loadTimeview(User user) {
        responsiveLayout.removeAllComponents();
        log.info("createTitleRow()");
        createTitleRow();
        log.info("createBudgetRow()");
        createBudgetRow();
        log.info("createHeadlineRow()");
        createHeadlineRow();
        log.info("createTimesheet()");
        createTimesheet(user);
        log.info("createFooterRow()");
        createFooterRow();
        log.info("Done creating timeview");
        //createReceiptRow();
    }

    private void createReceiptRow() {
        ResponsiveRow receiptRow = responsiveLayout.addRow().withAlignment(Alignment.MIDDLE_CENTER);
        receiptRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(customerExpenses);

        loadExistingReceipts();

    }

    private void loadExistingReceipts() {
        CustomerExpensesGrid customerExpensesGrid = new CustomerExpensesGrid();

        GridLayout gridExistingReceipts = customerExpensesGrid.getGridExistingReceipts();

        for (int i = 0; i < gridExistingReceipts.getColumns(); i++) {
            gridExistingReceipts.setColumnExpandRatio(i, 0);
        }
        gridExistingReceipts.setColumnExpandRatio(2, 1.0f);

        List<Contract> mainContracts = getMainContracts(contractService, dateButtons.getSelActiveUser().getValue());
        List<Project> allProjects = projectService.findAllByActiveTrueOrderByNameAsc();

        Set<Project> projectSet = mainContracts.stream().map(contract -> contractService.findProjectsByContractuuid(contract.getUuid())).flatMap(List::stream).distinct().filter(allProjects::contains).collect(Collectors.toSet());

        customerExpensesGrid.getCbProject().setItems(projectSet);
        customerExpensesGrid.getCbProject().setItemCaptionGenerator(item -> item.getClient().getName()+" - "+item.getName());

        customerExpensesGrid.getDfReceiptDate().setRangeEnd(lastDayOfMonth(currentDate));
        customerExpensesGrid.getDfReceiptDate().setRangeStart(currentDate.withDayOfMonth(1));


        receiptBinder.forField(customerExpensesGrid.getDfReceiptDate())
                //.asRequired("You must enter a date")
                .bind(Receipt::getReceiptdate, Receipt::setReceiptdate);
        receiptBinder.forField(customerExpensesGrid.getCbProject())
                //.asRequired("You must select a project")
                .bind(Receipt::getProject, Receipt::setProject);
        receiptBinder.forField(customerExpensesGrid.getTxtDescription())
                .bind(Receipt::getDescription, Receipt::setDescription);
        receiptBinder.forField(customerExpensesGrid.getTxtAmount())
                .withConverter(new StringToFloatConverter("Not a valid amount"))
                //.asRequired("Missing amount")
                .bind(Receipt::getAmount, Receipt::setAmount);

        receiptBinder.readBean(new Receipt());

        customerExpensesGrid.getBtnAddReceipt().addClickListener(event -> {
            Receipt receipt = new Receipt();
            try {
                receiptBinder.writeBean(receipt);
                receipt.setUser(dateButtons.getSelActiveUser().getValue());
                receiptsRepository.save(receipt);
                loadExistingReceipts();
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Receipt could not be saved, " +
                        "please check error messages for each field.");
            }
        });


        customerExpenses.getExpensesGridContainer().removeAllComponents();
        customerExpenses.getExpensesGridContainer().addComponent(customerExpensesGrid);

        int rows = 1;

        List<Receipt> receipts = receiptsRepository.findByUseruuidAndReceiptdateIsBetween(dateButtons.getSelActiveUser().getValue().getUuid(), currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).withDayOfMonth(1), currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).withDayOfMonth(currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).getMonth().length(currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).isLeapYear())));
        gridExistingReceipts.setRows(receipts.size()+2);
        for (Receipt receipt : receipts) {
            System.out.println("rows = " + rows);
            gridExistingReceipts.addComponent(new MLabel(receipt.getReceiptdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), 0, rows);
            gridExistingReceipts.addComponent(new MLabel(receipt.getProject().getName()), 1, rows);
            gridExistingReceipts.addComponent(new MLabel(receipt.getDescription()).withWidth(100, PERCENTAGE).withStyleName("cut_text"), 2, rows);
            gridExistingReceipts.addComponent(new MLabel(NumberConverter.formatCurrency(receipt.getAmount())), 3, rows);
            gridExistingReceipts.addComponent(new MButton("")
                    .withIcon(MaterialIcons.DELETE)
                    .withStyleName("borderless flat")
                    .withListener(event -> {
                        receiptsRepository.delete(receipt.getId());
                        loadExistingReceipts();
                    }), 4, rows);
            rows++;
        }

    }

    private void createTimesheet(User user) {
        sumHours = 0.0;
        weekDaySums = new WeekValues();
        List<Week> weeks = weekRestService.findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()), currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()), user.getUuid());
        LocalDate startOfWeek = currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);
        List<Work> workResources = workService.findByPeriodAndUserUUID(startOfWeek, endOfWeek.plusDays(1), user.getUuid());
        for (Work workResource : workResources.stream().filter(work -> work.getWorkduration() > 0.0).collect(Collectors.toList())) {
            if(weeks.stream().noneMatch(week -> week.getTaskuuid().equals(workResource.getTaskuuid()))) {
                Week week = new Week(UUID.randomUUID().toString(),
                        currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()),
                        currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()),
                        workResource.getUseruuid(),
                        workResource.getTaskuuid(),
                        workResource.getWorkas());
                weekRestService.save(week);
                weeks.add(week);
            }
        }

        List<WeekItem> weekItems = new ArrayList<>();
        for (Week week : weeks) {
            Task task = week.getTask();
            Hibernate.initialize(task);

            boolean onContract = isOnContract(week);
            if(!task.getProject().isActive() && !task.getProject().getClient().isActive()) onContract = false;
            WeekItem weekItem = new WeekItem(week, task, user, week.getWorkasUser(), !onContract);
            weekItem.setDate(startOfWeek);
            weekItems.add(weekItem);
            weekItem.setTaskname(task.getProject().getName() + " / " + task.getName());

            double sumTask = 0.0;
            for (Work work : workResources) {
                if(!work.getTaskuuid().equals(task.getUuid())) continue;
                sumHours += work.getWorkduration();
                sumTask += work.getWorkduration();
                setWeekItemAmounts(weekItem, work, work.getRegistered());
            }
            weekItem.setBudgetleft(sumTask);


        }
        log.info("sumHours = " + sumHours);

        weekRowTaskTitles.clear();
        for (WeekItem weekItem : weekItems) {
            log.info("weekItem = " + weekItem);
            List<Lesson> lessons = cultureRestService.findByUserAndProject(weekItem.getUser().getUuid(), weekItem.getTask().getProjectuuid());
            log.info("lessons.size() = " + lessons.size());
            boolean illegibleForLessonFramed = user.getUsername().equals("hans.lassen") &&
                    !weekItem.getTask().getProject().getClientuuid().equals("40c93307-1dfa-405a-8211-37cbda75318b") &&
                    weekItem.getWorkas() == null;
            log.info("illegibleForLessonFramed = " + illegibleForLessonFramed);
            if (illegibleForLessonFramed && lessons.size() == 0) { // Start of project
                log.info("Start of project");
                createLessonFramedTimeline(weekItem.getTask().getName(), weekItem.getTask().getProject(), "projectstart");
            } else if (illegibleForLessonFramed && lessons.stream().max(Comparator.comparing(Lesson::getRegistered)).get().getRegistered().isBefore(LocalDate.now().minusMonths(3))) { // 3 months since last registration
                log.info("3 months");
                createLessonFramedTimeline(weekItem.getTask().getName(), weekItem.getTask().getProject(), "3months");
            } else {
                log.info("*timeline");
                createTimeline(weekItem);
            }
        }

        // Find contracts that ended last month
        if(user.getUsername().equals("hans.lassen")) {
            log.info("LessonFramed");
            // Find all contracts that ended last month for current consultant
            for (Contract contract : contractService.findTimeActiveConsultantContracts(user, LocalDate.now().minusMonths(1)).stream().filter(contract -> contract.getActiveTo().isBefore(LocalDate.now().withDayOfMonth(1))).collect(Collectors.toList())) {
                log.info("Contract: "+contract);
                // Get the projects that are related to the contract as this project is likely ending for the consulant
                for (Project project : contractService.findProjectsByContractuuid(contract.getUuid())) {
                    log.info("project = " + project);
                    // Verify that the project doesn't continue in other contracts for the same consultant, so get all contracts for that project
                    for (Contract projectContract : project.getContracts()) {
                        log.info("projectContract = " + projectContract);
                        // If the projectContract (test) is before current contract then it is not relevant and current contract might still be the last one for the project and consultant.
                        if(projectContract.getActiveTo().isBefore(contract.activeTo)) {
                            log.info("projectContract is before current contract");
                            continue;
                        }
                        // Verify that the contract is related to the consultant
                        for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                            log.info("contractConsultant = " + contractConsultant);
                            if(contractConsultant.getUseruuid().equals(user.getUuid())) {
                                // This contract is extending the current project, so it does not end after all. Lesson Framed should not be shown.
                                log.info("Project "+project.getUuid()+" is extended in contract "+projectContract.getUuid() +" for user "+user.getUsername());
                                return;
                            }
                        }
                    }


                    log.info("Project: "+project);
                    // Find Lesson entered in this month, when the project has ended. This would be the last Lesson entry for the project.
                    List<Lesson> lessons = cultureRestService.findByUserAndProject(user.getUuid(), project.getUuid());
                    log.info("lessons.size() = " + lessons.size());
                    lessons.forEach(System.out::println);
                    Optional<Lesson> any = lessons.stream().filter(lesson -> lesson.getRegistered().isAfter(LocalDate.now().withDayOfMonth(1).minusDays(1))).findAny();

                    log.info("Lesson: "+any);
                    //if (!any.isPresent()) UI.getCurrent().getNavigator().navigateTo(LessonFramedView.VIEW_NAME + "/" + project.getUuid()+"/projectend");
                    if (!any.isPresent()) createLessonFramedTimeline("", project, "projectend");
                }
            }
        }
    }

    public static void setWeekItemAmounts(WeekItem weekItem, Work work, LocalDate workDate) {
        switch (workDate.getDayOfWeek().getValue()) {
            case 1:
                weekItem.setMon(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 2:
                weekItem.setTue(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 3:
                weekItem.setWed(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 4:
                weekItem.setThu(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 5:
                weekItem.setFri(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 6:
                weekItem.setSat(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
            case 7:
                weekItem.setSun(NumberConverter.formatDouble(work.getWorkduration()+0));
                break;
        }
    }

    private void createTitleRow() {
        dateButtons.getTxtWeekNumber().setValue(currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())+"");
        ResponsiveRow titleRow = responsiveLayout.addRow().withAlignment(Alignment.MIDDLE_CENTER);
        titleRow.addColumn()
                .withDisplayRules(12, 12, 6, 8)
                .withComponent(new MLabel("Week "+currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())+" / "+currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear())).withStyleName("h3"));
        titleRow.addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(dateButtons, ResponsiveColumn.ColumnComponentAlignment.RIGHT);
    }

    private void createBudgetRow() {
        LocalDate firstMonth = this.currentDate;
        LocalDate secondMonth = this.currentDate.plusDays(7);

        ResponsiveRow budgetRow = responsiveLayout.addRow().withAlignment(Alignment.MIDDLE_CENTER);

        createMonthBudgetCards(firstMonth, budgetRow, firstMonth.getMonthValue()!=secondMonth.getMonthValue()?"medium-blue":"dark-blue");
        if(firstMonth.getMonthValue()!=secondMonth.getMonthValue()) createMonthBudgetCards(secondMonth, budgetRow, "dark-blue");
    }

    private void createMonthBudgetCards(LocalDate month, ResponsiveRow budgetRow, String color) {
        // TODO:FIX
        List<BudgetDocument> consultantBudgetList = budgetService.getConsultantBudgetHoursByMonthDocuments(dateButtons.getSelActiveUser().getValue().getUuid(), month); //new ArrayList<>();//statisticsService.getConsultantBudgetDataByMonth(dateButtons.getSelActiveUser().getValue(), month);

        for (BudgetDocument budgetDocument : consultantBudgetList) {
            double workSum = workService.findWorkOnContract(budgetDocument.getContract().getUuid()).stream()
                    .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(budgetDocument.getMonth().withDayOfMonth(1)) &&
                            work.getUseruuid().equals(budgetDocument.getUser().getUuid()))
                    .mapToDouble(Work::getWorkduration).sum();
            if(budgetDocument.getBudgetHours()<=0.0) continue;
            TopCardImpl topCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_ur.svg", budgetDocument.getClient().getName(), "contract "+budgetDocument.getMonth().format(DateTimeFormatter.ofPattern("MMMM")), workSum+" / "+Math.round(budgetDocument.getBudgetHours()), "dark-blue"));
            budgetRow.addColumn()
                    .withDisplayRules(6, 6, 4,3)
                    .withComponent(topCard, ResponsiveColumn.ColumnComponentAlignment.CENTER);
        }
    }

    private void createHeadlineRow() {
        ResponsiveRow headingRow = responsiveLayout.addRow()
                .withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL,true)
                .withStyleName("card-1");

        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withOffset(DisplaySize.LG, 4)
                .withOffset(DisplaySize.MD, 4)
                .withComponent(getDayNameTitle(0), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(1), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(2), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(3), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(4), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(5), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(getDayNameTitle(6), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        headingRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(new MLabel("SUM").withStyleName("h5"), ResponsiveColumn.ColumnComponentAlignment.RIGHT);
    }

    private MVerticalLayout getDayNameTitle(int weekDay) {
        String[] strDays = new String[] { "Mon", "Tue", "Wed", "Thu",
                "Fri", "Sat", "Sun" };
        return new MVerticalLayout(
                new MLabel(strDays[weekDay].toUpperCase()).withStyleName("h5").withHeight(25, PIXELS),
                new MLabel(currentDate.plusDays(weekDay).format(DateTimeFormatter.ofPattern("dd/MM"))).withStyleName("tiny light").withHeight(15, PIXELS)
        ).alignAll(Alignment.MIDDLE_CENTER).withHeight(40, PIXELS).withSpacing(false).withMargin(false);
    }

    private void createFooterRow() {
        MTextField txtMon = getFloatingTextField();
        MTextField txtTue = getFloatingTextField();
        MTextField txtWed = getFloatingTextField();
        MTextField txtThu = getFloatingTextField();
        MTextField txtFri = getFloatingTextField();
        MTextField txtSat = getFloatingTextField();
        MTextField txtSun = getFloatingTextField();
        weekValuesBinder.bind(txtMon, WeekValues::getMonString, null);
        weekValuesBinder.bind(txtTue, WeekValues::getTueString, null);
        weekValuesBinder.bind(txtWed, WeekValues::getWedString, null);
        weekValuesBinder.bind(txtThu, WeekValues::getThuString, null);
        weekValuesBinder.bind(txtFri, WeekValues::getFriString, null);
        weekValuesBinder.bind(txtSat, WeekValues::getSatString, null);
        weekValuesBinder.bind(txtSun, WeekValues::getSunString, null);

        ResponsiveRow footerRow = responsiveLayout.addRow()
                .withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL,true)
                .withAlignment(Alignment.MIDDLE_CENTER)
                .withStyleName("card-1");

        footerRow.addColumn()
                .withVisibilityRules(true, true, true, true)
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(footerButtons, ResponsiveColumn.ColumnComponentAlignment.LEFT);
        createFooterSumField(txtMon, footerRow);
        createFooterSumField(txtTue, footerRow);
        createFooterSumField(txtWed, footerRow);
        createFooterSumField(txtThu, footerRow);
        createFooterSumField(txtFri, footerRow);
        createFooterSumField(txtSat, footerRow);
        createFooterSumField(txtSun, footerRow);
        footerRow.addColumn().withDisplayRules(1, 1, 1, 1).withVisibilityRules(false, false, true,true);

        ResponsiveRow sumRow = responsiveLayout.addRow().withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL, true);
        sumRow.withMargin(true).withMargin(ResponsiveRow.MarginSize.SMALL);
        sumRow.addColumn().withDisplayRules(12, 12, 9, 9).withVisibilityRules(false, false, true, true);
        MTextField sumTextField = new MTextField("week total:", sumHours + "")
                .withStyleName("floating")
                .withReadOnly(true);
        weekValuesBinder.bind(sumTextField, WeekValues::sum, null);
        sumRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 2, 2)
                .withComponent(sumTextField, ResponsiveColumn.ColumnComponentAlignment.CENTER);

        weekValuesBinder.readBean(weekDaySums);
    }

    private void createFooterSumField(MTextField txtDayField, ResponsiveRow footerRow) {
        footerRow.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(txtDayField, ResponsiveColumn.ColumnComponentAlignment.CENTER);
    }

    private MTextField getFloatingTextField() {
        return new MTextField().withWidth(100, PERCENTAGE)
                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                .withStyleName("borderless")
                .withReadOnly(true);
    }

    private void createTimeline(WeekItem weekItem) {
        weekDaySums.addWeekItem(weekItem);

        ResponsiveRow time1Row = responsiveLayout.addRow()
                .withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL, true)
                .withVerticalSpacing(ResponsiveRow.SpacingSize.SMALL, true)
                .withMargin(true)
                .withMargin(ResponsiveRow.MarginSize.SMALL)
                .withAlignment(Alignment.MIDDLE_CENTER);

        String projectName = weekItem.getTaskname().split("/")[0];
        String taskName = weekItem.getTaskname().split("/")[1];
        TaskTitle taskTitle = new TaskTitle();
        taskTitle.getLblDescription().setValue(weekItem.getTask().getProject().getCustomerreference());
        taskTitle.getTxtProjectname().setValue(projectName);
        taskTitle.getTxtTaskname().setValue(taskName);
        taskTitle.getBtnDelete().setIcon(MaterialIcons.DELETE);
        taskTitle.getBtnDelete().addClickListener(event -> {
            if (weekItem.getWeekItemSum() > 0.0) {
                Notification.show("Cannot remove row!", "Cannot remove row as long as you have registered hours on the task this week", Notification.Type.WARNING_MESSAGE);
                return;
            }
            weekRestService.delete(weekItem.getWeek().getUuid());
            responsiveLayout.removeComponent(time1Row);
        });
        File photo = photoService.getRelatedPhoto(weekItem.getTask().getProject().getClient().getUuid());
        if (photo != null && photo.getFile() != null && photo.getFile().length > 0) {
            taskTitle.getImgLogo().setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getFile()),
                    photo.getRelateduuid() + ".jpg"));
        } else {
            taskTitle.getImgLogo().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }

        if (weekItem.isLocked()) {
            Image image = new Image();
            image.setSource(new ThemeResource("images/icons/lock.png"));
            image.setDescription("No active contract");
            image.setHeight(30, PIXELS);
            taskTitle.getImgConsultant().addComponent(image);
        } else {
            User workas = weekItem.getWorkas();
            if (workas != null) {
                Image memberImage = photoService.getRoundMemberImage(workas.getUuid(), false, 30, PIXELS);
                memberImage.setDescription("Helping " + workas.getFirstname() + " " + workas.getLastname());
                taskTitle.getImgConsultant().addComponent(memberImage);
            }
        }

        weekRowTaskTitles.add(taskTitle);

        User workingAs = (weekItem.getWorkas() != null) ? weekItem.getWorkas() : weekItem.getUser();
        Task task = weekItem.getTask();

        final MLabel lblWeekItemSum = new MLabel(NumberUtils.round(weekItem.getBudgetleft(), 2) + "").withStyleName("h5");

        time1Row.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(taskTitle, ResponsiveColumn.ColumnComponentAlignment.LEFT);

        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(1, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getMon(), event -> {
                            double delta = updateTimefield(weekItem, 0, event);
                            weekDaySums.mon += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));

        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(2, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getTue(), event -> {
                            double delta = updateTimefield(weekItem, 1, event);
                            weekDaySums.tue += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(3, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getWed(), event -> {
                            double delta = updateTimefield(weekItem, 2, event);
                            weekDaySums.wed += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(4, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getThu(), event -> {
                            double delta = updateTimefield(weekItem, 3, event);
                            weekDaySums.thu += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(5, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getFri(), event -> {
                            double delta = updateTimefield(weekItem, 4, event);
                            weekDaySums.fri += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(6, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getSat(), event -> {
                            double delta = updateTimefield(weekItem, 5, event);
                            weekDaySums.sat += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(checkIfDisabled(7, weekItem, workingAs, task,
                        new MTextField(null, weekItem.getSun(), event -> {
                            double delta = updateTimefield(weekItem, 6, event);
                            weekDaySums.sun += delta;
                            weekItem.addBudget(delta);
                            lblWeekItemSum.setValue(NumberUtils.round(weekItem.getBudgetleft(), 2) + "");
                            updateSums();
                        }).withValueChangeMode(ValueChangeMode.BLUR)
                                .withWidth(100, PERCENTAGE)
                                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                                .withStyleName("floating")));


            time1Row.addColumn()
                    .withVisibilityRules(false, false, true, true)
                    .withDisplayRules(12, 12, 1, 1)
                    .withComponent(lblWeekItemSum, ResponsiveColumn.ColumnComponentAlignment.RIGHT);

    }

    private void createLessonFramedTimeline(String taskname, Project project, String action) {
        ResponsiveRow time1Row = responsiveLayout.addRow()
                .withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL, true)
                .withVerticalSpacing(ResponsiveRow.SpacingSize.SMALL, true)
                .withMargin(true)
                .withMargin(ResponsiveRow.MarginSize.SMALL)
                .withAlignment(Alignment.MIDDLE_CENTER);

        TaskTitle taskTitle = new TaskTitle();
        taskTitle.getLblDescription().setValue(project.getCustomerreference());
        taskTitle.getTxtProjectname().setValue(project.getName());
        taskTitle.getTxtTaskname().setValue(taskname);
        taskTitle.getBtnDelete().setIcon(MaterialIcons.DELETE);
        taskTitle.getBtnDelete().setEnabled(false);
        File photo = photoService.getRelatedPhoto(project.getClientuuid());
        if (photo != null && photo.getFile().length > 0) {
            taskTitle.getImgLogo().setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getFile()),
                    photo.getRelateduuid() + ".jpg"));
        } else {
            taskTitle.getImgLogo().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }

        weekRowTaskTitles.add(taskTitle);

        final MLabel lblWeekItemSum = new MLabel(NumberUtils.round(0.0, 2) + "").withStyleName("h5");

        time1Row.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(taskTitle, ResponsiveColumn.ColumnComponentAlignment.LEFT);

        time1Row.addColumn().withDisplayRules(12, 12, 7, 7).withComponent(new MButton("Click this button to evaluate project").withFullWidth().withListener(event ->
                UI.getCurrent().getNavigator().navigateTo(LessonFramedView.VIEW_NAME + "/" + project.getUuid() + "/" + action)));

        time1Row.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(lblWeekItemSum, ResponsiveColumn.ColumnComponentAlignment.RIGHT);

    }

    private MTextField checkIfDisabled(int weekday, WeekItem weekItem, User workingAs, Task task, MTextField mTextField) {
        boolean onContract = isOnContract(weekItem.getDate().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), weekday), workingAs.getUuid(), task);
        if(!onContract) return mTextField.withEnabled(false);

        // check if item is last month and user is not project manager. Project managers should be able to edit old timesheets
        boolean isLastMonth = weekItem.getDate().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), weekday).withDayOfMonth(2).isBefore(LocalDate.now().withDayOfMonth(2));
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        User owner = Optional.ofNullable(weekItem.getTask().getProject().getOwner()).orElse(new User());
        boolean isLocked = weekItem.getTask().getProject().isLocked();

        boolean isProjectOwner = owner.getUuid().equals(user.getUuid());
        if(!isLocked) return mTextField.withEnabled(true);
        if(isLastMonth && !isProjectOwner) return mTextField.withEnabled(false);

        return mTextField;
    }

    private double updateTimefield(WeekItem weekItem, int day, HasValue.ValueChangeEvent<String> event) {
        double weekDaySumDelta = 0.0;
        LocalDate workDate = weekItem.getDate().plusDays(day);
        try {
            if (event.getValue().trim().equals("")) {
                saveWork(weekItem, event, workDate);
                ((TextField) event.getComponent()).getValueChangeTimeout();
                return -nf.parse(event.getOldValue()).doubleValue();
            }
        } catch (ParseException e) {
            log.warn("Gammel værdi var ikke et tal: "+event.getOldValue());
        }
        try {
            weekDaySumDelta -= nf.parse(event.getOldValue()).doubleValue();
        } catch (ParseException e) {
            log.warn("Gammel værdi var ikke et tal: "+event.getOldValue());
        }
        try {
            weekDaySumDelta += nf.parse(event.getValue()).doubleValue();
        } catch (ParseException e) {
            log.warn("Ny værdi er ikke et tal: "+event.getValue());
            return weekDaySumDelta;
        }
        saveWork(weekItem, event, workDate);
        return weekDaySumDelta;
    }

    private void saveWork(WeekItem weekItem, HasValue.ValueChangeEvent<String> event, LocalDate workDate) {
        try {
            double newValue = event.getValue().equals("")?0.0:nf.parse(event.getValue()).doubleValue();
            Work work;
            if(weekItem.getWorkas()==null) {
                work = new Work(workDate, newValue, weekItem.getUser(), weekItem.getTask());
            } else {
                work = new Work(workDate, newValue, weekItem.getUser(), weekItem.getTask(), weekItem.getWorkas());
            }
            workService.save(work);
            if(!event.getValue().equals("")) event.getSource().setValue(nf.format(newValue));
        } catch (Exception e) {
            Notification.show("Error saving work", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            log.error("Could not create work for weekItem " + weekItem, e);
        }
    }

    private void updateSums() {
        weekValuesBinder.readBean(weekDaySums);
    }

    private boolean isOnContract(Week week) {
        boolean result = false;
        LocalDate localDateStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).weekBasedYear(), week.getYear()).with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), week.getWeeknumber()).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate localDateEnd = LocalDate.now().with(WeekFields.of(Locale.getDefault()).weekBasedYear(), week.getYear()).with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), week.getWeeknumber()).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);
        if(isOnContract(localDateStart, (week.getWorkasUser()!=null)?week.getWorkasUser().getUuid():week.getUseruuid(), week.getTask())) result = true;
        if(isOnContract(localDateEnd, (week.getWorkasUser()!=null)?week.getWorkasUser().getUuid():week.getUseruuid(), week.getTask())) result = true;
        return result;
    }

    private boolean isOnContract(LocalDate localDate, String useruuid, Task task) {
        return contractService.findConsultantRate(localDate, useruuid, task, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED)!=null;
    }

    private class WeekValues {
        private double mon = 0.0;
        private double tue = 0.0;
        private double wed = 0.0;
        private double thu = 0.0;
        private double fri = 0.0;
        private double sat = 0.0;
        private double sun = 0.0;

        public WeekValues() {
        }

        public double getSun() {
            return sun;
        }

        public void setSun(double sun) {
            this.sun = sun;
        }

        public void addMon(double value) {
            this.mon += value;
        }

        public void addTue(double value) {
            this.tue += value;
        }

        public void addWed(double value) {
            this.wed += value;
        }

        public void addThu(double value) {
            this.thu += value;
        }

        public void addFri(double value) {
            this.fri += value;
        }

        public void addSat(double value) {
            this.sat += value;
        }

        public void addSun(double value) {
            this.sun += value;
        }

        public String getMonString() {
            return nf.format(mon);
        }

        public String getTueString() {
            return nf.format(tue);
        }

        public String getWedString() {
            return nf.format(wed);
        }

        public String getThuString() {
            return nf.format(thu);
        }

        public String getFriString() {
            return nf.format(fri);
        }

        public String getSatString() {
            return nf.format(sat);
        }

        public String getSunString() {
            return nf.format(sun);
        }

        public String sum() {
            return nf.format(mon + tue + wed + thu + fri + sat + sun);
        }

        public void addWeekItem(WeekItem weekItem) {
            try {
                addMon(nf.parse(weekItem.getMon()).doubleValue());
                addTue(nf.parse(weekItem.getTue()).doubleValue());
                addWed(nf.parse(weekItem.getWed()).doubleValue());
                addThu(nf.parse(weekItem.getThu()).doubleValue());
                addFri(nf.parse(weekItem.getFri()).doubleValue());
                addSat(nf.parse(weekItem.getSat()).doubleValue());
                addSun(nf.parse(weekItem.getSun()).doubleValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}

