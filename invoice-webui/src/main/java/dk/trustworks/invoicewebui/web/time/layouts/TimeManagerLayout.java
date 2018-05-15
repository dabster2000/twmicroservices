package dk.trustworks.invoicewebui.web.time.layouts;


import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.TimeService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.components.DateButtons;
import dk.trustworks.invoicewebui.web.time.components.FooterButtons;
import dk.trustworks.invoicewebui.web.time.components.TaskTitle;
import dk.trustworks.invoicewebui.web.time.components.TimeManagerImpl;
import dk.trustworks.invoicewebui.web.time.model.WeekItem;
import org.hibernate.Hibernate;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringComponent
@SpringUI
public class TimeManagerLayout extends ResponsiveLayout {

    private static final Logger log = LoggerFactory.getLogger(TimeManagerImpl.class);

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private TimeService timeService;

    private ResponsiveLayout responsiveLayout;

    private LocalDate currentDate = LocalDate.now().withDayOfWeek(1);

    private final FooterButtons footerButtons;
    private final DateButtons dateButtons;

    private NumberFormat nf = NumberFormat.getInstance();
    private Binder<WeekValues> weekValuesBinder = new Binder<>();
    private WeekValues weekDaySums;
    private double sumHours = 0.0;

    private final List<TaskTitle> weekRowTaskTitles = new ArrayList<>();

    public TimeManagerLayout() {
        footerButtons = new FooterButtons();
        dateButtons = new DateButtons();
        responsiveLayout = new ResponsiveLayout(ContainerType.FLUID);

        dateButtons.getBtnWeekNumberDecr().addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            log.info("currentDate.minusWeeks(1) = " + currentDate);
            //setDateFields();
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        dateButtons.getBtnWeekNumberIncr().addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            log.info("currentDate.plusWeeks(1) = " + currentDate);
            //setDateFields();
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });
        /*
        getBtnYearDecr().addClickListener(event -> {
            currentDate = currentDate.minusYears(1);
            log.info("currentDate.minusYears(1) = " + currentDate);
            setDateFields();
            updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        getBtnYearIncr().addClickListener(event -> {
            currentDate = currentDate.plusYears(1);
            log.info("currentDate.plusYears(1) = " + currentDate);
            setDateFields();
            updateGrid(getSelActiveUser().getSelectedItem().get());
        });
        */

        dateButtons.getSelActiveUser().addValueChangeListener(event -> loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get()));

        footerButtons.getBtnCopyWeek().setIcon(MaterialIcons.CONTENT_COPY);
        footerButtons.getBtnCopyWeek().addClickListener(event1 -> {
            log.info("getBtnCopyWeek()");
            timeService.cloneTaskToWeek(currentDate.getWeekOfWeekyear(), currentDate.getYear(), dateButtons.getSelActiveUser().getSelectedItem().get());
            loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
            //loadData(getSelActiveUser().getSelectedItem().get());
        });

        footerButtons.getBtnEdit().setIcon(MaterialIcons.EDIT);
        footerButtons.getBtnEdit().addClickListener(event -> {
            for (TaskTitle weekRowTaskTitle : weekRowTaskTitles) {
                weekRowTaskTitle.getImgLogo().setVisible(!weekRowTaskTitle.getImgLogo().isVisible());
                weekRowTaskTitle.getBtnDelete().setVisible(!weekRowTaskTitle.getBtnDelete().isVisible());
            }
        });

        footerButtons.getBtnAddTask().setIcon(MaterialIcons.PLAYLIST_ADD);
        footerButtons.getBtnAddTask().addClickListener((Button.ClickEvent event) -> {
            log.info("getBtnAddTask()");
            final Window window = new Window("Add Task");
            window.setWidth(300.0f, Unit.PIXELS);
            window.setHeight(500.0f, Unit.PIXELS);
            window.setModal(true);

            List<Client> clientResources = clientRepository.findByActiveTrue();
            ComboBox<Client> clientComboBox = new ComboBox<>();
            clientComboBox.setItemCaptionGenerator(Client::getName);
            clientComboBox.setWidth("100%");
            clientComboBox.setEmptySelectionAllowed(false);
            clientComboBox.setEmptySelectionCaption("select client");
            List<Client> clients = new ArrayList<>();
            for (Client clientResource : clientResources) {
                clients.add(clientResource);
            }
            clientComboBox.setItems(clients);

            ComboBox<Project> projectComboBox = new ComboBox<>();
            projectComboBox.setItemCaptionGenerator(Project::getName);
            projectComboBox.setWidth("100%");
            projectComboBox.setEmptySelectionAllowed(false);
            projectComboBox.setVisible(false);

            ComboBox<Task> taskComboBox = new ComboBox<>();
            taskComboBox.setItemCaptionGenerator(Task::getName);
            taskComboBox.setWidth("100%");
            taskComboBox.setEmptySelectionAllowed(false);
            taskComboBox.setVisible(false);

            Button addTaskButton = new Button("add task");
            addTaskButton.addStyleName("flat friendly");
            addTaskButton.setEnabled(false);

            clientComboBox.addValueChangeListener(event1 -> {
                taskComboBox.setVisible(false);
                addTaskButton.setEnabled(false);

                //List<Project> projects = clientRepository.findOne(event1.getValue().getUuid()).getProjects();
                List<Project> projects = projectRepository.findByClientAndActiveTrueOrderByNameAsc(clientComboBox.getValue());

                projectComboBox.clear();
                projectComboBox.setItems(projects);
                projectComboBox.setVisible(true);
            });

            projectComboBox.addValueChangeListener(event1 -> {
                addTaskButton.setEnabled(false);
                List<Task> tasks = new ArrayList<>();
                taskComboBox.clear();
                if(event1.getValue()==null) return;
                for (Task task : projectRepository.findOne(event1.getValue().getUuid()).getTasks()) {
                    tasks.add(task);
                }
                taskComboBox.setItems(tasks);
                taskComboBox.setVisible(true);
            });

            taskComboBox.addValueChangeListener(event1 -> {
                addTaskButton.setEnabled(false);
                if(event1.getValue()==null) return;
                addTaskButton.setEnabled(true);
            });

            addTaskButton.addClickListener(event1 -> {
                weekRepository.save(new Week(UUID.randomUUID().toString(),
                        currentDate.getWeekOfWeekyear(),
                        currentDate.getYear(),
                        dateButtons.getSelActiveUser().getValue(),
                        taskComboBox.getSelectedItem().get()));
                window.close();
                loadTimeview(dateButtons.getSelActiveUser().getSelectedItem().get());
                //loadData(getSelActiveUser().getSelectedItem().get());
            });

            window.setContent(new VerticalLayout(clientComboBox, projectComboBox, taskComboBox, addTaskButton));
            this.getUI().addWindow(window);
        });
    }

    public ResponsiveLayout init() {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        System.out.println("userSession = " + userSession);

        if(userSession == null) return new ResponsiveLayout();

        List<User> users = userRepository.findByActiveTrueOrderByUsername();
        dateButtons.getSelActiveUser().setItemCaptionGenerator(User::getUsername);
        dateButtons.getSelActiveUser().setItems(users);
        // find userSession user
        for (User user : users) {
            if(user.getUuid().equals(userSession.getUser().getUuid())) dateButtons.getSelActiveUser().setSelectedItem(user);
        }
        User user = dateButtons.getSelActiveUser().getSelectedItem().get();

        loadTimeview(user);

        //Card card = new Card();
        this.addRow().addColumn()
                .withDisplayRules(12, 12, 10, 10)
                .withOffset(DisplaySize.LG, 1)
                .withOffset(DisplaySize.MD, 1)
                .withComponent(responsiveLayout);
        //card.getCardHolder().addComponent(responsiveLayout);
        return this;
    }

    private void loadTimeview(User user) {
        responsiveLayout.removeAllComponents();
        createTitleRow();
        createHeadlineRow();
        createTimesheet(user);
        createFooterRow();
    }

    private void createTimesheet(User user) {
        sumHours = 0.0;
        weekDaySums = new WeekValues();
        List<Week> weeks = weekRepository.findByWeeknumberAndYearAndUserOrderBySortingAsc(currentDate.getWeekOfWeekyear(), currentDate.getYear(), user);
        LocalDate startOfWeek = currentDate.withDayOfWeek(1);
        log.info("startOfWeek = " + startOfWeek);
        LocalDate endOfWeek = currentDate.withDayOfWeek(7);
        log.info("endOfWeek = " + endOfWeek);
        List<Work> workResources = workRepository.findByPeriodAndUserUUID(startOfWeek.toString("yyyy-MM-dd"), endOfWeek.toString("yyyy-MM-dd"), user.getUuid());
        for (Work workResource : workResources) {
            if(!weeks.stream().filter(week -> week.getTask().getUuid().equals(workResource.getTask().getUuid())).findFirst().isPresent()) {
                Week week = new Week(UUID.randomUUID().toString(),
                        currentDate.getWeekOfWeekyear(),
                        currentDate.getYear(),
                        workResource.getUser(),
                        workResource.getTask());
                weekRepository.save(week);
                weeks.add(week);
            }
        }

        log.info("workResources.size() = " + workResources.size());
        List<WeekItem> weekItems = new ArrayList<>();
        for (Week week : weeks) {
            log.info("week = " + week);
            Task task = week.getTask();
            log.info("task = " + task);
            Hibernate.initialize(task);

            WeekItem weekItem = new WeekItem(week, task, user);
            weekItem.setDate(startOfWeek);
            weekItems.add(weekItem);
            weekItem.setTaskname(task.getProject().getName() + " / " + task.getName());
            Double budgetLeftByTaskuuidAndUseruuid = 0.0;
            try {
                budgetLeftByTaskuuidAndUseruuid = budgetRepository.findBudgetLeftByTaskuuidAndUseruuid(task.getUuid(), user.getUuid());
            } catch (Exception e) {
                Notification.show("Error loading budget...", Notification.Type.TRAY_NOTIFICATION);
                e.printStackTrace();
            }
            if(budgetLeftByTaskuuidAndUseruuid!=null) weekItem.setBudgetleft(budgetLeftByTaskuuidAndUseruuid);
            for (Work work : workResources) {
                if(!work.getTask().getUuid().equals(task.getUuid())) continue;
                log.info("work = " + work);
                sumHours += work.getWorkduration();
                LocalDate workDate = new LocalDate(work.getYear(), work.getMonth()+1, work.getDay());
                switch (workDate.getDayOfWeek()) {
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
        }
        log.info("sumHours = " + sumHours);

        weekRowTaskTitles.clear();
        for (WeekItem weekItem : weekItems) {
            createTimeline(weekItem);
        }
    }

    private void createTitleRow() {
        dateButtons.getTxtWeekNumber().setValue(currentDate.getWeekOfWeekyear()+"");
        ResponsiveRow titleRow = responsiveLayout.addRow().withAlignment(Alignment.MIDDLE_CENTER);
        titleRow.addColumn()
                .withDisplayRules(12, 12, 6, 8)
                .withComponent(new MLabel("Week "+currentDate.getWeekOfWeekyear()+" / "+currentDate.getYear()).withStyleName("h3"));
        titleRow.addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(dateButtons, ResponsiveColumn.ColumnComponentAlignment.RIGHT);
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
                .withComponent(new MLabel("BUDGET").withStyleName("h5"), ResponsiveColumn.ColumnComponentAlignment.RIGHT);
    }

    private MVerticalLayout getDayNameTitle(int weekDay) {
        String[] strDays = new String[] { "Mon", "Tue", "Wed", "Thu",
                "Fri", "Sat", "Sun" };
        return new MVerticalLayout(
                new MLabel(strDays[weekDay].toUpperCase()).withStyleName("h5").withHeight(25, Unit.PIXELS),
                new MLabel(currentDate.plusDays(weekDay).toString("dd/MM")).withStyleName("tiny light").withHeight(15, Unit.PIXELS)
        ).alignAll(Alignment.MIDDLE_CENTER).withHeight(40, Unit.PIXELS).withSpacing(false).withMargin(false);
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
        return new MTextField().withWidth(100, Unit.PERCENTAGE)
                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                .withStyleName("borderless")
                .withReadOnly(true);
    }

    private void createTimeline(WeekItem weekItem) {
        weekDaySums.addWeekItem(weekItem);
        ResponsiveRow time1Row = responsiveLayout.addRow()
                .withHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL,true)
                .withVerticalSpacing(ResponsiveRow.SpacingSize.SMALL, true)
                .withMargin(true)
                .withMargin(ResponsiveRow.MarginSize.SMALL)
                .withAlignment(Alignment.MIDDLE_CENTER);

        String projectName = weekItem.getTaskname().split("/")[0];
        String taskName = weekItem.getTaskname().split("/")[1];
        TaskTitle taskTitle = new TaskTitle();
        taskTitle.getTxtProjectname().setValue(projectName);
        taskTitle.getTxtTaskname().setValue(taskName);
        taskTitle.getBtnDelete().setIcon(MaterialIcons.DELETE);
        taskTitle.getBtnDelete().addClickListener(event -> {
            if(weekItem.getWeekItemSum() > 0.0) {
                Notification.show("Cannot remove row!", "Cannot remove row as long as you have registered hours on the task this week", Notification.Type.WARNING_MESSAGE);
                return;
            }
            weekRepository.delete(weekItem.getWeek());
            responsiveLayout.removeComponent(time1Row);
        });
        Photo photo = photoRepository.findByRelateduuid(weekItem.getTask().getProject().getClient().getUuid());
        if(photo!=null && photo.getPhoto().length > 0) {
            taskTitle.getImgLogo().setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()),
                    "logo.jpg"));
        } else {
            taskTitle.getImgLogo().setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        weekRowTaskTitles.add(taskTitle);

        time1Row.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(taskTitle, ResponsiveColumn.ColumnComponentAlignment.LEFT);
        MTextField mTextField = new MTextField(null, weekItem.getMon(), event -> {
            weekDaySums.mon += updateTimefield(weekItem, 0, event);
            updateSums();
        })
                .withWidth(100, Unit.PERCENTAGE)
                .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER, "floating");
        Responsive.makeResponsive(mTextField);

        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(mTextField);
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getTue(), event -> {
                    weekDaySums.tue += updateTimefield(weekItem, 1, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getWed(), event -> {
                    weekDaySums.wed += updateTimefield(weekItem, 2, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getThu(), event -> {
                    weekDaySums.thu += updateTimefield(weekItem, 3, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getFri(), event -> {
                    weekDaySums.fri += updateTimefield(weekItem, 4, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getSat(), event -> {
                    weekDaySums.sat += updateTimefield(weekItem, 5, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withDisplayRules(12, 12, 1,1)
                .withComponent(new MTextField(null, weekItem.getSun(), event -> {
                    weekDaySums.sun += updateTimefield(weekItem, 6, event);
                    updateSums();
                })
                        .withWidth(100, Unit.PERCENTAGE)
                        .withStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER)
                        .withStyleName("floating"));
        time1Row.addColumn()
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 1, 1)
                .withComponent(new MLabel(weekItem.getBudgetleft()+"").withStyleName("h5"), ResponsiveColumn.ColumnComponentAlignment.RIGHT);
    }

    private double updateTimefield(WeekItem weekItem, int day, HasValue.ValueChangeEvent<String> event) {
        double weekDaySumDelta = 0.0;
        LocalDate workDate = weekItem.getDate().plusDays(day);
        try {
            if (event.getValue().trim().equals("")) {
                saveWork(weekItem, event, workDate);
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
            Work work = new Work(workDate.getDayOfMonth(), workDate.getMonthOfYear() - 1, workDate.getYear(), newValue, weekItem.getUser(), weekItem.getTask());
            workRepository.save(work);
            if(!event.getValue().equals("")) event.getSource().setValue(nf.format(newValue));
        } catch (ParseException e) {
            log.error("Could not save work for weekItem " + weekItem, e);
        }
    }

    private void updateSums() {
        weekValuesBinder.readBean(weekDaySums);
        //sumHours = weekDaySums.sum();
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
