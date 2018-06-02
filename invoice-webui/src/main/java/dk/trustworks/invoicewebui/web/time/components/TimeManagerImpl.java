package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.TimeService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.model.WeekItem;
import org.hibernate.Hibernate;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tm.kod.widgets.numberfield.NumberField;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 16/08/2017.
 */
@SpringComponent
@SpringUI
public class TimeManagerImpl extends TimeManagerDesign {

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
    private TimeService timeService;

    private LocalDate currentDate = LocalDate.now().withDayOfWeek(1);//new LocalDate(2017, 02, 015);//LocalDate.now();

    public TimeManagerImpl() {
        getBtnWeekNumberDecr().addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            log.info("currentDate.minusWeeks(1) = " + currentDate);
            setDateFields();
            updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        getBtnWeekNumberIncr().addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            log.info("currentDate.plusWeeks(1) = " + currentDate);
            setDateFields();
            updateGrid(getSelActiveUser().getSelectedItem().get());
        });

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

        getSelActiveUser().addValueChangeListener(event -> updateGrid(getSelActiveUser().getSelectedItem().get()));

        getBtnCopyWeek().addClickListener(event1 -> {
            log.info("getBtnCopyWeek()");
            timeService.cloneTaskToWeek(currentDate.getWeekOfWeekyear(), currentDate.getYear(), getSelActiveUser().getSelectedItem().get());
            loadData(getSelActiveUser().getSelectedItem().get());
        });

        getBtnAddTask().addClickListener((Button.ClickEvent event) -> {
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
                        getSelActiveUser().getValue(),
                        taskComboBox.getSelectedItem().get()));
                window.close();
                loadData(getSelActiveUser().getSelectedItem().get());
            });

            window.setContent(new VerticalLayout(clientComboBox, projectComboBox, taskComboBox, addTaskButton));
            this.getUI().addWindow(window);
        });

        getGridTimeTable().getEditor().addSaveListener(event -> {
            LocalDate saveDate = this.currentDate;
            log.info("saveDate = " + saveDate);
            log.info("event.getBean() = " + event.getBean());
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getMon()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getTue()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getWed()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getThu()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getFri()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getSat()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            saveDate = saveDate.plusDays(1);
            workRepository.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getSun()),
                    event.getBean().getUser(),
                    event.getBean().getTask()));
            loadData(getSelActiveUser().getSelectedItem().get());
        });
    }

    @Transactional
    public TimeManagerImpl init() {
        log.info("TimeManagerImpl.init");
        setDateFields();
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        System.out.println("userSession = " + userSession);
        if(userSession == null) return this;

        List<User> users = userRepository.findByActiveTrue();
        getSelActiveUser().setItemCaptionGenerator(User::getUsername);
        getSelActiveUser().setItems(users);

        // find userSession user
        for (User user : users) {
            if(user.getUuid().equals(userSession.getUser().getUuid())) getSelActiveUser().setSelectedItem(user);
        }

        updateGrid(getSelActiveUser().getSelectedItem().get());
        return this;
    }

    private void updateGrid(User user) {
        log.info("TimeManagerImpl.updateGrid");
        log.info("user = [" + user + "]");
        loadData(user);

        setGridHeaderLabels();

        setGridColumns();

        getGridTimeTable().setSelectionMode(Grid.SelectionMode.NONE);

        getGridTimeTable().getEditor().setEnabled(true);
        getGridTimeTable().getEditor().setBuffered(true);
    }

    private void setGridColumns() {
        Binder<WeekItem> binder = getGridTimeTable().getEditor().getBinder();

        Binder.Binding<WeekItem, String> monBinding = binder.bind(createInstance(), WeekItem::getMon, WeekItem::setMon);
        getGridTimeTable()
                .getColumn("mon")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(monBinding);

        Binder.Binding<WeekItem, String> tueBinding = binder.bind(createInstance(), WeekItem::getTue, WeekItem::setTue);
        getGridTimeTable()
                .getColumn("tue")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(tueBinding);

        Binder.Binding<WeekItem, String> wedBinding = binder.bind(createInstance(), WeekItem::getWed, WeekItem::setWed);
        getGridTimeTable()
                .getColumn("wed")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(wedBinding);

        Binder.Binding<WeekItem, String> thuBinding = binder.bind(createInstance(), WeekItem::getThu, WeekItem::setThu);
        getGridTimeTable()
                .getColumn("thu")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(thuBinding);

        Binder.Binding<WeekItem, String> friBinding = binder.bind(createInstance(), WeekItem::getFri, WeekItem::setFri);
        getGridTimeTable()
                .getColumn("fri")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(friBinding);

        Binder.Binding<WeekItem, String> satBinding = binder.bind(createInstance(), WeekItem::getSat, WeekItem::setSat);
        getGridTimeTable()
                .getColumn("sat")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(satBinding);

        Binder.Binding<WeekItem, String> sunBinding = binder.bind(createInstance(), WeekItem::getSun, WeekItem::setSun);
        getGridTimeTable()
                .getColumn("sun")
                .setStyleGenerator(day -> "centeralign")
                .setWidth(85)
                .setEditorBinding(sunBinding);

        getGridTimeTable()
                .getColumn("budgetleft")
                .setWidth(125);
    }

    private void setGridHeaderLabels() {
        HeaderRow mainHeader = getGridTimeTable().getDefaultHeaderRow();
        mainHeader.getCell("mon").setHtml("<center>Mon</center>");
        mainHeader.getCell("tue").setHtml("<center>Tue</center>");
        mainHeader.getCell("wed").setHtml("<center>Wed</center>");
        mainHeader.getCell("thu").setHtml("<center>Thu</center>");
        mainHeader.getCell("fri").setHtml("<center>Fri</center>");
        mainHeader.getCell("sat").setHtml("<center>Sat</center>");
        mainHeader.getCell("sun").setHtml("<center>Sun</center>");
    }

    private void loadData(User user) {
        log.info("TimeManagerImpl.loadData");
        log.info("user = [" + user + "]");
        List<Week> weeks = weekRepository.findByWeeknumberAndYearAndUserOrderBySortingAsc(currentDate.getWeekOfWeekyear(), currentDate.getYear(), user);
        log.info("weeks.size() = " + weeks.size());
        if(weeks.size()>0) getBtnCopyWeek().setEnabled(false);
        else getBtnCopyWeek().setEnabled(true);
        LocalDate startOfWeek = currentDate.withDayOfWeek(1);
        log.info("startOfWeek = " + startOfWeek);
        LocalDate endOfWeek = currentDate.withDayOfWeek(7);
        log.info("endOfWeek = " + endOfWeek);
        List<Work> workResources = workRepository.findByPeriodAndUserUUID(startOfWeek.toString("yyyy-MM-dd"), endOfWeek.toString("yyyy-MM-dd"), user.getUuid());
        log.info("workResources.size() = " + workResources.size());

        List<WeekItem> weekItems = new ArrayList<>();
        double sumHours = 0.0;
        for (Week week : weeks) {
            log.info("week = " + week);
            Task task = week.getTask();
            log.info("task = " + task);
            Hibernate.initialize(task);


            WeekItem weekItem = new WeekItem(week, task, user, false);
            weekItems.add(weekItem);
            weekItem.setTaskname(task.getProject().getName() + " / " + task.getName());
            Double budgetLeftByTaskuuidAndUseruuid = 0.0;//budgetRepository.findBudgetLeftByTaskuuidAndUseruuid(task.getUuid(), user.getUuid());
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
        getLblTotalHours().setValue(NumberConverter.formatDouble(sumHours));
        getGridTimeTable().setItems(weekItems);
    }

    public NumberField createInstance() {
        log.info("TimeManagerImpl.createInstance");
        NumberField field = new NumberField("Amount");
        field.setSigned(true);                                                 // disable negative sign, default true
        field.setUseGrouping(true);                                        // enable grouping, default false
        field.setGroupingSeparator('.');                                  // set grouping separator ' '
        field.setDecimalLength(2);
        field.setDecimalSeparator(',');                                  // custom converter
        return field;
    }

    private void setDateFields() {
        log.info("TimeManagerImpl.setDateFields");
        getTxtWeekNumber().setValue(currentDate.getWeekOfWeekyear()+"");
        log.info("Text Weeknumber = " + currentDate.getWeekOfWeekyear());
        getTxtYear().setValue(currentDate.getYear()+"");
        log.info("Text Year = " + currentDate.getYear());
        getLblCurrentDate().setValue(currentDate.toString("dd. MMM yyyy") + " - " + currentDate.withDayOfWeek(7).toString("dd. MMM yyyy"));
        log.info("Top Dates = "+(currentDate.toString("dd. MMM yyyy") + " - " + currentDate.withDayOfWeek(7).toString("dd. MMM yyyy")));
    }
}
