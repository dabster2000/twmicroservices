package dk.trustworks.invoicewebui.web.project.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ConsultantRowDesign;
import dk.trustworks.invoicewebui.web.contracts.model.BudgetRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static java.util.Comparator.comparing;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * Created by hans on 21/08/2017.
 */

@SpringComponent
@SpringUI
public class ProjectManagerImpl extends ProjectManagerDesign {

    private final UserRepository userRepository;

    private final ContractService contractService;

    private final ProjectService projectService;

    private final TaskRepository taskRepository;

    private final ClientRepository clientRepository;

    private final BudgetNewRepository budgetNewRepository;

    private final PhotoRepository photoRepository;

    private final PhotoService photoService;

    private final NewsRepository newsRepository;

    private ResponsiveLayout responsiveLayout;

    private Project currentProject;

    private BudgetCardDesign budgetCard;

    private Grid<BudgetRow> grid;

    private VerticalLayout consultantsLayout;
    private MVerticalLayout contractLayout;
    private MVerticalLayout tasksLayout;


    @Autowired
    public ProjectManagerImpl(UserRepository userRepository, ProjectService projectService, TaskRepository taskRepository, ClientRepository clientRepository, ClientdataRepository clientdataRepository, BudgetNewRepository budgetNewRepository, PhotoRepository photoRepository, PhotoService photoService, NewsRepository newsRepository, ContractService contractService) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
        this.budgetNewRepository = budgetNewRepository;
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.newsRepository = newsRepository;
        this.contractService = contractService;

        getBtnAddNewProject().addClickListener((Button.ClickEvent event) -> {
            final Window window = new Window("Create Project");
            window.setWidth("330px");
            window.setHeight("300px");
            window.setModal(true);
            NewProjectDesign newProject = new NewProjectDesign();
            window.setContent(newProject);
            UI.getCurrent().addWindow(window);
            newProject.getCbClients().setItems(clientRepository.findByActiveTrueOrderByName());
            newProject.getCbClients().addValueChangeListener(event1 -> {
                List<Clientdata> clientdataList = clientdataRepository.findByClient(event1.getValue());
                newProject.getCbClientdatas().setVisible(true);
                newProject.getCbClientdatas().setItems(clientdataList);
                newProject.getCbClientdatas().setItemCaptionGenerator(item -> item.getStreetnamenumber() + ", "
                        + item.getPostalcode() + " " + item.getCity() + ", "
                        + item.getContactperson());
                newProject.getCbClientdatas().setSelectedItem(clientdataList.get(0));
                newProject.getBtnCreate().setEnabled(true);
            });
            newProject.getCbClients().setItemCaptionGenerator(Client::getName);
            newProject.getBtnCreate().addClickListener(event1 -> {
                window.close();
                UI.getCurrent().removeWindow(window);
                Clientdata clientdata = newProject.getCbClientdatas().getValue();
                Project project = projectService.save(new Project(newProject.getTxtProjectName().getValue(), newProject.getCbClients().getValue(), clientdata));
                getOnOffSwitch().setValue(false);
                getSelClient().clear();
                List<Project> reloadedProjects = newArrayList(projectService.findAllByActiveTrueOrderByNameAsc());
                getSelProject().setItems(reloadedProjects);
                setCurrentProject(project.getUuid());
            });
            newProject.getBtnCancel().addClickListener(event1 -> window.close());
        });

        getSelProject().addValueChangeListener(event -> reloadGrid());
        getOnOffSwitch().addValueChangeListener(event -> changeOptions());
        getSelClient().addValueChangeListener(event -> changeOptions());
    }


    @Transactional
    public ProjectManagerImpl init() {
        getOnOffSwitch().setValue(false);
        getSelProject().setItemCaptionGenerator(Project::getName);
        List<Project> projects = newArrayList(((getOnOffSwitch().getValue()) ? projectService.findAllByOrderByNameAsc() : projectService.findAllByActiveTrueOrderByNameAsc()));

        getSelProject().setItems(projects);

        getSelClient().setItems(clientRepository.findByActiveTrueOrderByName());
        getSelClient().setItemCaptionGenerator(Client::getName);

        return this;
    }

    private void changeOptions() {
        if(!getSelClient().getSelectedItem().isPresent()) {
            List<Project> clientProjectList = newArrayList(((getOnOffSwitch().getValue())? projectService.findAllByOrderByNameAsc(): projectService.findAllByActiveTrueOrderByNameAsc()));
            getSelProject().setItems(clientProjectList);
        } else {
            if(getOnOffSwitch().getValue()) {
                getSelProject().setItems(getSelClient().getSelectedItem().get().getProjects().stream().sorted(comparing(Project::getName)));
            } else {
                getSelProject().setItems(getSelClient().getSelectedItem().get().getProjects().stream().filter(Project::isActive).sorted(comparing(Project::getName)));
            }
        }
        //reloadGrid();
    }

    public void setCurrentProject(String projectUUID) {
        currentProject = projectService.findOne(projectUUID);
        getSelProject().setSelectedItem(currentProject);
        getSelProject().setValue(currentProject);
        //reloadGrid();
    }

    private void createDetailLayout() {
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Photo photoResource = photoRepository.findByRelateduuid(currentProject.getClient().getUuid());
        ProjectDetailCardImpl projectDetailCard = new ProjectDetailCardImpl(currentProject, userRepository.findAll(), photoResource, projectService, newsRepository, userRepository);
        projectDetailCard.getBtnUpdate().addClickListener(event -> {
            projectDetailCard.save();
            updateTreeGrid();
        });
        projectDetailCard.getBtnDelete().addClickListener(event -> {
            projectService.delete(currentProject);
            getSelClient().clear();
            getSelProject().clear();
            getOnOffSwitch().setValue(false);
            getSelProject().setItems(projectService.findAllByActiveTrueOrderByNameAsc());
            reloadGrid();
        });
        if(currentProject.getTasks().stream().anyMatch(task -> !task.getType().equals(TaskType.SO))) projectDetailCard.getBtnDelete().setVisible(false);
        else projectDetailCard.getBtnDelete().setVisible(true);
        ResponsiveRow clientDetailsRow = responsiveLayout.addRow();
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectDetailCard);

        Card tasksCard = new Card();
        tasksCard.getLblTitle().setValue("Tasks");
        tasksLayout = new MVerticalLayout().withWidth(100, PERCENTAGE);
        tasksCard.getContent().addComponent(tasksLayout);
        createTasksList();
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(tasksCard);

        Card consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");
        consultantsLayout = new MVerticalLayout().withWidth(100, PERCENTAGE);
        consultantsCard.getContent().addComponent(consultantsLayout);
        createConsultantList();
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsCard);

        if(currentProject.getContracts().size()>0) {
            Card contractCard = new Card();
            contractCard.getLblTitle().setValue("Contracts");
            contractLayout = new MVerticalLayout().withWidth(100, PERCENTAGE);
            contractCard.getContent().addComponent(contractLayout);
            createContractChart();
            clientDetailsRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(contractCard);
        }

        budgetCard = new BudgetCardDesign();
        updateTreeGrid();

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(budgetCard);
    }

    private void createTasksList() {
        tasksLayout.removeAllComponents();
        for (Task task : currentProject.getTasks()) {
            TaskRowDesign taskRow = new TaskRowDesign();
            taskRow.getLblName().setValue(task.getName());
            taskRow.getTxtName().setVisible(false);
            if(task.getWorkList().size()>0 || task.getType()==TaskType.SO)  taskRow.getBtnDelete().setVisible(false);
            taskRow.getBtnDelete().setIcon(MaterialIcons.DELETE);
            taskRow.getBtnDelete().addClickListener(event -> {
                taskRepository.delete(task.getUuid());
                currentProject.getTasks().remove(task);
                reloadGrid();
            });
            taskRow.getCssTaskName().addLayoutClickListener(event -> {
                taskRow.getHlChart().removeAllComponents();
                if(taskRow.getHlChart().isVisible()) {
                    taskRow.getHlChart().setVisible(false);
                } else {
                    taskRow.getHlChart().addComponent(createTopGrossingConsultantsChart(task));
                    taskRow.getHlChart().setVisible(true);
                }
            });
            tasksLayout.add(taskRow);
        }
        TaskRowDesign newTaskRow = new TaskRowDesign();
        newTaskRow.getLblName().setVisible(false);
        newTaskRow.getCssTaskName().setVisible(false);
        newTaskRow.getBtnDelete().setIcon(MaterialIcons.ADD);
        newTaskRow.getBtnDelete().addClickListener(event -> {
            Task task = taskRepository.save(new Task(newTaskRow.getTxtName().getValue(), currentProject));
            currentProject.getTasks().add(task);
            reloadGrid();
        });
        tasksLayout.add(newTaskRow);
    }

    private Chart createTopGrossingConsultantsChart(Task task) {
        System.out.println("ConsultantHoursPerMonthChart.createTopGrossingConsultantsChart");
        //System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        //Period period = new Period(periodStart, periodEnd, PeriodType.months());
        Chart chart = new Chart();
        chart.setWidth(100, PERCENTAGE);
        chart.setHeight(100, PIXELS);

        //chart.setCaption("");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.BAR);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getxAxis().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        XAxis x = new XAxis();
        x.setCategories(task.getName());
        chart.getConfiguration().addxAxis(x);

        YAxis y = new YAxis();
        y.setCategories("Consultant");
        y.setVisible(false);
        chart.getConfiguration().addyAxis(y);

        PlotOptionsSeries plot = new PlotOptionsSeries();
        plot.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plot);


        Map<User, Double> userWork = new HashMap<>();
        List<Work> workList = task.getWorkList();

        for (Work work : workList) {
            userWork.putIfAbsent(work.getUser(), 0.0);
            userWork.put(work.getUser(), userWork.get(work.getUser())+work.getWorkduration());
        }

        for (User user : userWork.keySet()) {
            chart.getConfiguration().addSeries(new ListSeries(user.getFirstname()+" "+user.getLastname(), userWork.get(user)));
        }

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private void createContractChart() {
        contractLayout.removeAllComponents();
        Chart chart = new Chart(ChartType.COLUMNRANGE);
        contractLayout.addComponent(chart);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();
        conf.getChart().setInverted(true);

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        for (Contract mainContract : currentProject.getContracts()) {
            for (Consultant consultant : mainContract.getConsultants()) {
                xAxis.addCategory(consultant.getUser().getUsername());
            }
        }
        xAxis.setVisible(false);

        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("");
        yAxis.setType(AxisType.DATETIME);
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.dateFormat('%b %y', this.point.low) + ' - ' + Highcharts.dateFormat('%b %y', this.point.high)");
        conf.setTooltip(tooltip);

        PlotOptionsColumnrange columnRange = new PlotOptionsColumnrange();
        columnRange.setGrouping(false);
        DataLabelsRange dataLabels = new DataLabelsRange(true);
        dataLabels
                .setFormatter("this.y == this.point.low ? '' : this.series.name");
        dataLabels.setInside(true);
        dataLabels.setColor(new SolidColor("white"));
        columnRange.setDataLabels(dataLabels);

        conf.setPlotOptions(columnRange);

        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        for (Contract mainContract : currentProject.getContracts()) {
            for (Consultant consultant : mainContract.getConsultants()) {
                DataSeries ls = new DataSeries(consultant.getUser().getUsername());
                DataSeriesItem dataSeriesItem = new DataSeriesItem();
                dataSeriesItem.setName(consultant.getUser().getUsername());
                dataSeriesItem.setLow(mainContract.getActiveFrom().atStartOfDay().toEpochSecond(ZoneOffset.UTC)*1000);
                dataSeriesItem.setHigh(mainContract.getActiveTo().atStartOfDay().toEpochSecond(ZoneOffset.UTC)*1000);
                ls.add(dataSeriesItem);
                /*
                for (SubContract subContract : mainContract.getChildren()) {
                    ls.add(new DataSeriesItem(subContract.getActiveTo().atStartOfDay().toInstant(ZoneOffset.UTC), contractNumber));
                }
                */
                conf.addSeries(ls);
            }

        }

        chart.drawChart(conf);
    }

    private void createConsultantList() {
        consultantsLayout.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        consultantsLayout.addComponent(responsiveLayout);
        ResponsiveRow responsiveRow = responsiveLayout.addRow();

        for (Contract mainContract : currentProject.getContracts().stream().sorted(Comparator.comparing(Contract::getActiveTo).reversed()).collect(Collectors.toList())) {
            for (Consultant consultant : mainContract.getConsultants()) {
                ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
                consultantRowDesign.getLblName().setValue(consultant.getUser().getFirstname() + " " + consultant.getUser().getLastname());
                consultantRowDesign.getTxtRate().setValue(Math.round(consultant.getRate()) + "");
                consultantRowDesign.getTxtRate().setReadOnly(true);
                consultantRowDesign.getTxtHours().setValue(Math.round(consultant.getHours()) + "");
                consultantRowDesign.getTxtHours().setReadOnly(true);
                consultantRowDesign.getVlHours().setVisible(consultant.getContract().getContractType().equals(ContractType.PERIOD));
                consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));
                consultantRowDesign.getBtnDelete().setVisible(false);
                if(mainContract.getActiveTo().isBefore(LocalDate.now().withDayOfMonth(1))) {
                    consultantRowDesign.getHlBackground().setStyleName("bg-grey");
                    consultantRowDesign.getHlNameBackground().setStyleName("dark-grey");
                }

                responsiveRow.addColumn()
                        .withComponent(consultantRowDesign)
                        .withDisplayRules(12, 12, 12, 12);
            }
        }
    }

    private void updateTreeGrid() {
        budgetCard.getContainer().removeAllComponents();
        if(currentProject.getContracts().stream().anyMatch(contract -> contract.getContractType().equals(ContractType.AMOUNT))) {
            grid = createGrid();
            ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            ResponsiveRow row = responsiveLayout.addRow();
            row.addColumn().withDisplayRules(12, 12, 8, 8).withComponent(grid).setGrow(true);
            row.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(createUsedBudgetChart());
            budgetCard.getContainer().addComponent(responsiveLayout);
        }
    }

    private Chart createUsedBudgetChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Contract Budget"));

        List<Contract> contractList = currentProject.getContracts().stream().sorted(Comparator.comparing(Contract::getName)).collect(Collectors.toList());

        XAxis xAxis = new XAxis();
        xAxis.setCategories(contractList.stream().map(Contract::getName).toArray(String[]::new));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("");
        yAxis.setMin(0);
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        conf.addyAxis(yAxis);

        conf.getLegend().setEnabled(false);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        DataLabels labels = new DataLabels(true);
        Style style=new Style();
        style.setTextShadow("0 0 3px black");
        labels.setStyle(style);
        labels.setColor(new SolidColor("white"));
        plotOptions.setDataLabels(labels);
        conf.setPlotOptions(plotOptions);

        ListSeries restSeries = new ListSeries("rest");
        conf.addSeries(restSeries);
        ListSeries budgetSeries = new ListSeries("budget");
        conf.addSeries(budgetSeries);
        ListSeries usedSeries = new ListSeries("used");
        conf.addSeries(usedSeries);
        for (Contract contract : contractList) {
            if(contract.getContractType().equals(ContractType.PERIOD)) continue;
            double used = contractService.findAmountUsedOnContract(contract);

            LocalDate startDate = LocalDate.now().withDayOfMonth(1);
            LocalDate endDate = currentProject.getContracts().stream().max(Comparator.comparing(Contract::getActiveTo)).get().getActiveTo();

            double budgetSum = 0.0;
            for (Consultant consultant : contract.getConsultants()) {
                LocalDate budgetDate = startDate;
                while (budgetDate.isBefore(endDate)) {
                    final LocalDate filterDate = budgetDate;
                    BudgetNew budget = budgetNewRepository.findByMonthAndYearAndConsultant(filterDate.getMonthValue() - 1, filterDate.getYear(), consultant);
                    if(budget!=null) budgetSum += budget.getBudget();
                    budgetDate = budgetDate.plusMonths(1);
                }
            }

            double rest = contract.getAmount() - used - budgetSum;

            usedSeries.addData(used);
            budgetSeries.addData(budgetSum);
            restSeries.addData(rest<0.0?0.0:rest);
        }

        chart.drawChart(conf);
        return chart;
    }

    private Grid createGrid() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        if(currentProject.getContracts().size()==0) return new Grid();
        LocalDate endDate = currentProject.getContracts().stream().max(Comparator.comparing(Contract::getActiveTo)).get().getActiveTo();
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);

        grid = new Grid<>();

        grid.addColumn(BudgetRow::getUsername).setWidth(150).setCaption("Consultant").setId("name-column");
        grid.addColumn(budgetRow -> budgetRow.getConsultant().getContract().getName()).setWidth(100).setCaption("Contract").setId("contract-column");
        grid.setFrozenColumnCount(2);
        grid.setWidth("100%");
        grid.getEditor().setEnabled(true);

        List<BudgetRow> budgetRows = new ArrayList<>();

        for (Contract mainContract : currentProject.getContracts()) {
            if(mainContract.getActiveTo().isBefore(startDate)) continue;
            if(!mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) continue;
            for (Consultant consultant : mainContract.getConsultants()) {
                BudgetRow budgetRow = new BudgetRow(consultant, (int)(monthsBetween+1));
                LocalDate budgetDate = startDate;

                int month = 0;
                while(budgetDate.isBefore(mainContract.getActiveTo())) {
                    final LocalDate filterDate = budgetDate;

                    BudgetNew budget = budgetNewRepository.findByMonthAndYearAndConsultant(filterDate.getMonthValue()-1, filterDate.getYear(), consultant);

                    if(budget != null) {
                        budgetRow.setMonth(month, (budget.getBudget() / consultant.getRate())+"");
                    } else {
                        budgetNewRepository.save(new BudgetNew(filterDate.getMonthValue()-1, filterDate.getYear(), 0.0, consultant));
                        budgetRow.setMonth(month, "0.0");
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }
                budgetRows.add(budgetRow);
            }
            int year = startDate.getYear();
            LocalDate budgetDate = startDate;
            while(budgetDate.isBefore(endDate)) {
                budgetDate = budgetDate.plusMonths(1);
                if(year < budgetDate.getYear()) {
                    year++;
                }
            }
        }

        int month = 0;
        int year = startDate.getYear();
        LocalDate budgetDate = startDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            final int actualMonth = month;
            grid.addColumn(
                    taskRow -> taskRow.getMonth(actualMonth))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setWidth(100)
                    .setId(Month.of(filterDate.getMonthValue()).name()+filterDate.getYear())
                    .setCaption(Month.of(filterDate.getMonthValue()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+filterDate.format(DateTimeFormatter.ofPattern("yy")))
                    .setEditorComponent(new TextField(), (Setter<BudgetRow, String>) (taskRow, budgetValue) -> taskRow.setMonth(actualMonth, budgetValue));
            budgetDate = budgetDate.plusMonths(1);
            month++;
            if(year < budgetDate.getYear()) {
                year++;
            }
        }

        grid.getEditor().setEnabled(true);
        grid.getEditor().addSaveListener(event -> {
            BudgetRow budgetRow = event.getBean();
            LocalDate budgetCountDate = startDate;
            for (String budgetString : budgetRow.getBudget()) {
                if(budgetCountDate.isAfter(budgetRow.getConsultant().getContract().getActiveTo())) continue;
                if(budgetCountDate.isBefore(budgetRow.getConsultant().getContract().getActiveFrom())) continue;
                if(budgetString==null) budgetString = "0.0";
                BudgetNew budget = budgetNewRepository.findByMonthAndYearAndConsultant(
                        budgetCountDate.getMonthValue() - 1,
                        budgetCountDate.getYear(),
                        budgetRow.getConsultant());
                budget.setBudget(Double.parseDouble(budgetString) * NumberConverter.parseDouble(budgetRow.getRate()));
                budgetNewRepository.save(budget);
                budgetCountDate = budgetCountDate.plusMonths(1);
            }
            updateTreeGrid();
        });
        grid.setItems(budgetRows);
        if(budgetRows.size() == 0) budgetCard.setVisible(false);

        return grid;
    }


    /*
    private TreeGrid createTreeGrid() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = new LocalDate(currentProject.getEnddate().getYear(),
                currentProject.getEnddate().getMonthValue(),
                currentProject.getEnddate().getDayOfMonth());
        Months monthsBetween = Months.monthsBetween(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween.getMonths());

        List<TaskRow> taskRows = new ArrayList<>();

        Map<String, User> usersMap = userRepository.findAll().stream().collect(Collectors.toMap(User::getUuid, user -> user));

        for (Task task : currentProject.getTasks()) {
            List<Budget> budgets = budgetRepository.findByTaskuuid(task.getUuid());
            TaskRow taskRow = new TaskRow(task, monthsBetween.getMonths()+1);
            taskRows.add(taskRow);
            for (User user : usersMap.values()) {
                LocalDate budgetDate = startDate;
                List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTask(task);
                Optional<Taskworkerconstraint> taskworkerconstraint = taskworkerconstraints.stream()
                        .filter(p ->
                                p.getTask()!=null &&
                                p.getUser()!=null &&
                                p.getTask().getUuid().equals(task.getUuid()) &&
                                p.getUser().getUuid().equals(user.getUuid()))
                        .findFirst();
                //if(user.getUsername().equals("hans.lassen")) System.out.println("taskworkerconstraint = " + taskworkerconstraint);

                if(!taskworkerconstraint.isPresent()) continue;

                UserRow userRow = new UserRow(task, taskworkerconstraint.get(), monthsBetween.getMonths()+1, user);

                //if(user.getUsername().equals("hans.lassen")) System.out.println("budgets = " + budgets.size());

                int month = 0;
                while(budgetDate.isBefore(endDate)) {
                    final LocalDate filterDate = budgetDate;

                    Optional<Budget> budget = budgets.stream()
                            .filter(p -> p.getYear()==filterDate.getYear() &&
                                    p.getMonth()==filterDate.getMonthOfYear()-1 &&
                                    p.getTask()!=null &&
                                    p.getUser()!=null &&
                                    p.getTask().getUuid().equals(task.getUuid()) &&
                                    p.getUser().getUuid().equals(user.getUuid()))
                            .findFirst();

                    if(budget.isPresent()) {
                        if(user.getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget);
                        userRow.setMonth(month, (budget.get().getBudget() / taskworkerconstraint.get().getPrice())+"");
                    } else {
                        userRow.setMonth(month, "0.0");
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }
                taskRow.addUserRow(userRow);
                System.out.println("userRow = " + userRow);
            }
            System.out.println("taskRow = " + taskRow);
        }


        treeGrid = new TreeGrid<>();
        treeGrid.addColumn(TaskRow::getTaskName).setWidth(200).setCaption("Task Name").setId("name-column").setEditorComponent(new TextField(), TaskRow::setTaskName);
        treeGrid.addColumn(TaskRow::getUsername).setWidth(200).setCaption("Consultant");
        treeGrid.addColumn(TaskRow::getRate).setWidth(100).setCaption("Rate").setEditorComponent(new TextField(), TaskRow::setRate);
        treeGrid.setFrozenColumnCount(3);

        GridContextMenu<TaskRow> gridMenu = new GridContextMenu<>(treeGrid);
        gridMenu.addGridBodyContextMenuListener(this::updateGridBodyMenu);
        gridMenu.addGridHeaderContextMenuListener(this::updateGridHeaderMenu);

        int month = 0;
        int year = startDate.getYear();
        List<String> yearColumns = new ArrayList<>();
        LocalDate budgetDate = startDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            final int actualMonth = month;
            Grid.Column<TaskRow, ?> budgetColumn = treeGrid.addColumn(
                    taskRow -> taskRow.getMonth(actualMonth))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setWidth(100)
                    .setId(Month.of(filterDate.getMonthOfYear()).name()+filterDate.getYear())
                    .setCaption(Month.of(filterDate.getMonthOfYear()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+filterDate.year().getAsShortText())
                    .setEditorComponent(new TextField(), (Setter<TaskRow, String>) (taskRow, budgetValue) -> taskRow.setMonth(actualMonth, budgetValue));
            yearColumns.add(budgetColumn.getId());
            budgetDate = budgetDate.plusMonths(1);
            month++;
            if(year < budgetDate.getYear()) {
                //topHeader.join(yearColumns.toArray(new String[0])).setText(year + "");
                yearColumns = new ArrayList<>();
                year++;
            }
        }

        treeGrid.setWidth("100%");
        treeGrid.getEditor().setEnabled(true);
        treeGrid.getEditor().addSaveListener(event -> {
            TaskRow taskRow = event.getBean();
            if(taskRow.getClass().equals(UserRow.class)) {
                UserRow userRow = (UserRow) taskRow;
                Taskworkerconstraint taskworkerconstraint = userRow.getTaskworkerconstraint();
                System.out.println("taskworkerconstraint = " + taskworkerconstraint);
                taskworkerconstraint.setPrice(Double.parseDouble(userRow.getRate()));
                taskworkerconstraintRepository.save(taskworkerconstraint);
                LocalDate budgetCountDate = startDate;
                List<Budget> budgetList = new ArrayList<>();
                for (String budgetString : userRow.getBudget()) {
                    if(budgetString==null) budgetString = "0.0";
                    Budget budget = new Budget(
                            budgetCountDate.getMonthOfYear()-1,
                            budgetCountDate.getYear(),
                            Double.parseDouble(budgetString) * taskworkerconstraint.getPrice(),
                            userRow.getUser(),
                            userRow.getTask()
                    );
                    budgetList.add(budget);
                    budgetCountDate = budgetCountDate.plusMonths(1);
                }

                budgetRepository.save(budgetList);
                //updateTreeGrid();
            } else {
                taskRow.getTask().setName(taskRow.getTaskName());
                taskRepository.save(taskRow.getTask());
                //updateTreeGrid();
            }
        });
        treeGrid.setItems(taskRows, TaskRow::getUserRows);
        return treeGrid;
    }
    */

    private void reloadGrid() {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        currentProject = getSelProject().getValue();
        if(getSelProject().getSelectedItem().isPresent()) createDetailLayout();
    }
/*
    private void updateGridBodyMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getItem() != null) {
            if(event.getItem().getClass().equals(TaskRow.class)) {
                event.getContextMenu().addItem("Add Consultant to "+((TaskRow)event.getItem()).getTaskName(), VaadinIcons.PLUS, selectedItem -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add consultant"));
                    ComboBox<User> userComboBox = new ComboBox<>();
                    userComboBox.setItems(userRepository.findAll());
                    userComboBox.setItemCaptionGenerator(User::getUsername);
                    subContent.addComponent(userComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> {
                        Taskworkerconstraint taskworkerconstraint = new Taskworkerconstraint(0.0, userComboBox.getSelectedItem().get(), ((TaskRow) event.getItem()).getTask());
                        taskworkerconstraintRepository.save(taskworkerconstraint);
                        subWindow.close();
                        updateTreeGrid();
                    });
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                });
            } else {
                event.getContextMenu().addItem("Remove "+((UserRow)event.getItem()).getUsername(), VaadinIcons.CLOSE,
                        selectedItem -> Notification.show("Not possible at this time!"));
            }
        } else {
            event.getContextMenu().addItem("Add Task", VaadinIcons.PLUS, selectedItem -> {
                Task task = new Task("new task", currentProject);
                currentProject.getTasks().add(task);
                taskRepository.save(task);
                updateTreeGrid();
            });
        }
    }

    private void updateGridHeaderMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getColumn() != null) {
            event.getContextMenu().addItem("Sort Ascending", selectedItem ->
                    treeGrid.sort((Grid.Column<TaskRow, ?>) event.getColumn(), SortDirection.ASCENDING));
            event.getContextMenu().addItem("Sort Descending", selectedItem ->
                    treeGrid.sort((Grid.Column<TaskRow, ?>) event.getColumn(), SortDirection.DESCENDING));
        } else {
            event.getContextMenu().addItem("menu is empty", null);
        }
    }
    */
}
