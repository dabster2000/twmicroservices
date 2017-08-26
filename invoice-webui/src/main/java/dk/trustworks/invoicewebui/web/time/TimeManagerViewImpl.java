package dk.trustworks.invoicewebui.web.time;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.network.clients.*;
import dk.trustworks.invoicewebui.network.dto.*;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.security.Authorizer;
import dk.trustworks.invoicewebui.web.time.model.WeekItem;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.vaadin.patrik.FastNavigation;
import tm.kod.widgets.numberfield.NumberField;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 16/08/2017.
 */
@SpringView(name = TimeManagerViewImpl.VIEW_NAME)
public class TimeManagerViewImpl extends TimeManagerViewDesign implements View {

    @Autowired
    ClientClient clientClient;

    @Autowired
    ProjectClient projectClient;

    @Autowired
    TaskClient taskClient;

    @Autowired
    UserClient userClient;

    @Autowired
    WeekClient weekClient;

    @Autowired
    MobileApiClient mobileApiClient;

    @Autowired
    WorkClient workClient;

    public static final String VIEW_NAME = "time";

    private LocalDate currentDate = LocalDate.now().withDayOfWeek(1);//new LocalDate(2017, 02, 015);//LocalDate.now();

    @PostConstruct
    void init() {
        setDateFields();
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession == null) return;

        getBtnWeekNumberDecr().addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            setDateFields();
            updateGrid(userSession);
        });

        getBtnWeekNumberIncr().addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            setDateFields();
            updateGrid(userSession);
        });

        getBtnYearDecr().addClickListener(event -> {
            currentDate = currentDate.minusYears(1);
            setDateFields();
            updateGrid(userSession);
        });

        getBtnYearIncr().addClickListener(event -> {
            currentDate = currentDate.plusYears(1);
            setDateFields();
            updateGrid(userSession);
        });

        getSelActiveUser().setItemCaptionGenerator(User::getUsername);

        List<User> users = new ArrayList<>();
        Resources<Resource<User>> userResources = userClient.findAllActiveUsers();
        for (Resource<User> userResource : userResources.getContent()) {
            users.add(userResource.getContent());
        }

        getSelActiveUser().setItems(users);

        // find userSession user
        for (User user : users) {
            if(user.getUuid().equals(userSession.getUuid())) getSelActiveUser().setSelectedItem(user);
        }

        updateGrid(userSession);

        getBtnAddTask().addClickListener(event -> {
            final Window window = new Window("Invoice editor");
            window.setWidth(300.0f, Unit.PIXELS);
            window.setHeight(500.0f, Unit.PIXELS);
            window.setModal(true);

            Resources<Resource<Client>> clientResources = clientClient.findAllActiveClients();
            ComboBox<Client> clientComboBox = new ComboBox<>();
            clientComboBox.setItemCaptionGenerator(Client::getName);
            clientComboBox.setWidth("100%");
            clientComboBox.setEmptySelectionAllowed(false);
            clientComboBox.setEmptySelectionCaption("select client");
            List<Client> clients = new ArrayList<>();
            for (Resource<Client> clientResource : clientResources) {
                clients.add(clientResource.getContent());
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

                List<Project> projects = new ArrayList<>();
                for (Resource<Project> projectResource : projectClient.findByClientuuidAndActiveTrue(event1.getValue().getUuid())) {
                    projects.add(projectResource.getContent());
                }
                projectComboBox.setSelectedItem(projects.get(0));
                projectComboBox.setItems(projects);
                projectComboBox.setVisible(true);
            });

            projectComboBox.addValueChangeListener(event1 -> {
                addTaskButton.setEnabled(false);
                List<Task> tasks = new ArrayList<>();
                for (Resource<Task> taskResource : taskClient.findByProjectuuid(event1.getValue().getUuid())) {
                    tasks.add(taskResource.getContent());
                }
                taskComboBox.setSelectedItem(tasks.get(0));
                taskComboBox.setItems(tasks);
                taskComboBox.setVisible(true);
            });

            taskComboBox.addValueChangeListener(event1 -> {
                addTaskButton.setEnabled(true);
            });

            addTaskButton.addClickListener(event1 -> {
                weekClient.save(new CreatedWeek(UUID.randomUUID().toString(),
                        currentDate.getWeekOfWeekyear(),
                        currentDate.getYear(),
                        getSelActiveUser().getValue().getUuid(),
                        taskComboBox.getSelectedItem().get().getUuid()));
                window.close();
                loadData(userSession);
            });

            window.setContent(new VerticalLayout(clientComboBox, projectComboBox, taskComboBox, addTaskButton));
            this.getUI().addWindow(window);
        });
    }

    private void updateGrid(UserSession userSession) {
        loadData(userSession);

        setGridHeaderLabels();

        setGridColumns();

        getGridTimeTable().setSelectionMode(Grid.SelectionMode.NONE);

        getGridTimeTable().getEditor().addSaveListener(event -> {
            System.out.println("event.getBean() = " + event.getBean());
            System.out.println("event.getBean() = " + event.getSource());
            LocalDate saveDate = this.currentDate;
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getMon()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getTue()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getWed()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getThu()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getFri()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getSat()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));
            saveDate = saveDate.plusDays(1);
            workClient.save(new Work(
                    saveDate.getDayOfMonth(),
                    saveDate.getMonthOfYear()-1,
                    saveDate.getYear(),
                    NumberConverter.parseDouble(event.getBean().getSun()),
                    event.getBean().getTaskuuid(),
                    event.getBean().getUseruuid()));

            loadData(userSession);
        });
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

    private void loadData(UserSession userSession) {
        Resources<Resource<Week>> weeks = mobileApiClient.findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(currentDate.getWeekOfWeekyear(), currentDate.getYear(), userSession.getUuid());
        LocalDate startOfWeek = currentDate.withDayOfWeek(1);
        LocalDate endOfWeek = currentDate.withDayOfWeek(7);
        Resources<Resource<Work>> workResources = workClient.findByPeriodAndUserUUID(startOfWeek.toString("yyyy-MM-dd"), endOfWeek.toString("yyyy-MM-dd"), userSession.getUuid());

        List<WeekItem> weekItems = new ArrayList<>();
        double sumHours = 0.0;
        for (Resource<Week> weekResource : weeks.getContent()) {
            System.out.println("weekResource.getContent() = " + weekResource.getContent());
            WeekItem weekItem = new WeekItem(weekResource.getContent().getTask().getContent().getUuid(), userSession.getUuid());
            weekItems.add(weekItem);
            weekItem.setTaskname(
                    weekResource.getContent().getTask().getContent().getProject().getName() + " / " +
                    weekResource.getContent().getTask().getContent().getName()
            );
            String taskUUID = weekResource.getContent().getTask().getContent().getUuid();
            System.out.println("taskUUID = " + taskUUID);
            for (Resource<Work> workResource : workResources.getContent()) {
                if(!workResource.getContent().getTaskuuid().equals(taskUUID)) continue;
                sumHours += workResource.getContent().getWorkduration();
                Work work = workResource.getContent();
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
        getLblTotalHours().setValue(NumberConverter.formatDouble(sumHours));
        getGridTimeTable().setItems(weekItems);
    }

    public NumberField createInstance() {
        NumberField field = new NumberField("Amount");
        field.setSigned(true);                                                 // disable negative sign, default true
        field.setUseGrouping(true);                                        // enable grouping, default false
        field.setGroupingSeparator('.');                                  // set grouping separator ' '
        field.setDecimalLength(2);
        field.setDecimalSeparator(',');                                  // custom converter
        return field;
    }

    private void initNavigation(final Grid grid) {
        FastNavigation nav = new FastNavigation(grid,false);
        nav.setChangeColumnAfterLastRow(false);


        nav.addRowEditListener(event -> {
            int rowIndex = event.getRowIndex();
            if (rowIndex >= 0) {
                /*
                Indexed ds = getGridTimeTable().;
                Object itemId = ds.getIdByIndex(rowIndex);
                printChangedRow(rowIndex, ds, itemId);
                */
                System.out.println("rowIndex = " + rowIndex);
            }

        });

        // Open with F2
        nav.addEditorOpenShortcut(ShortcutAction.KeyCode.F2);

        // Close with F3
        nav.addEditorCloseShortcut(ShortcutAction.KeyCode.F3);

        // Row focus change
        nav.addRowFocusListener(event -> System.out.println("Focus moved to row " + event.getRow()));


        // Cell focus change
        nav.addCellFocusListener(event -> {
            int row = event.getRow();
            int col = event.getColumn();
            System.out.println("Focus moved to cell [" + row + ", " + col + " ]");
        });

        nav.addRowEditListener(rowEditEvent -> {

        });

        // Listening to opening of editor
        nav.addEditorOpenListener(event -> {
            int row = event.getRow();
            System.out.println("Editor opened on row " + row + " at column " + event.getColumn());
        });

        // Listening to closing of editor
        nav.addEditorCloseListener(event -> {
            System.out.println("Editor closed on row " + event.getRow() + ", column " + event.getColumn() + ", " + (event.wasCancelled() ? "user cancelled change" : "user saved change"));

        });
    }

    private void setDateFields() {
        getTxtWeekNumber().setValue(currentDate.getWeekOfWeekyear()+"");
        getTxtYear().setValue(currentDate.getYear()+"");
        getLblCurrentDate().setValue(currentDate.toString("dd. MMM yyyy") + " - " + currentDate.plusDays(7).toString("dd. MMM yyyy"));
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Authorizer.authorize(this);
    }
}
