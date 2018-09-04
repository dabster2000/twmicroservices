package dk.trustworks.invoicewebui.web.project.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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

    //private Project currentProject;

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

        getSelProject().addValueChangeListener(event -> reloadGrid(Optional.ofNullable(getSelProject().getValue())));
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
        Project currentProject = projectService.findOne(projectUUID);
        getSelProject().setSelectedItem(currentProject);
        getSelProject().setValue(currentProject);
        //reloadGrid();
    }

    private void createDetailLayout(Project currentProject) {
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Photo photoResource = photoRepository.findByRelateduuid(currentProject.getClient().getUuid());
        ProjectDetailCardImpl projectDetailCard = new ProjectDetailCardImpl(currentProject, userRepository.findByOrderByUsername(), photoResource, projectService, newsRepository, userRepository);
        projectDetailCard.getBtnUpdate().addClickListener(event -> {
            projectDetailCard.save();
            updateTreeGrid(currentProject);
        });
        projectDetailCard.getBtnDelete().addClickListener(event -> {
            projectService.delete(currentProject);
            getSelClient().clear();
            getSelProject().clear();
            getOnOffSwitch().setValue(false);
            getSelProject().setItems(projectService.findAllByActiveTrueOrderByNameAsc());
            reloadGrid(Optional.empty());
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
        createTasksList(currentProject);
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(tasksCard);

        Card consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");
        consultantsLayout = new MVerticalLayout().withWidth(100, PERCENTAGE);
        consultantsCard.getContent().addComponent(consultantsLayout);
        createConsultantList(currentProject);
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsCard);

        if(currentProject.getContracts().size()>0) {
            Card contractCard = new Card();
            contractCard.getLblTitle().setValue("Contract Periods");
            contractLayout = new MVerticalLayout().withWidth(100, PERCENTAGE);
            contractCard.getContent().addComponent(contractLayout);
            createContractChart(currentProject);
            clientDetailsRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(contractCard);
        }

        budgetCard = new BudgetCardDesign();
        updateTreeGrid(currentProject);

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(budgetCard);
    }

    private void createTasksList(final Project currentProject) {
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
                reloadGrid(Optional.ofNullable(projectService.findOne(currentProject.getUuid())));
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
        newTaskRow.getTxtName().addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                if(!target.equals(newTaskRow.getTxtName())) return;
                Task task = taskRepository.save(new Task(newTaskRow.getTxtName().getValue(), currentProject));
                currentProject.getTasks().add(task);
                reloadGrid(Optional.of(projectService.save(currentProject)));
            }
        });
        newTaskRow.getBtnDelete().addClickListener(event -> {
            Task task = taskRepository.save(new Task(newTaskRow.getTxtName().getValue(), currentProject));
            currentProject.getTasks().add(task);
            reloadGrid(Optional.of(projectService.save(currentProject)));
        });
        tasksLayout.add(newTaskRow);
    }

    private Chart createTopGrossingConsultantsChart(Task task) {
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

    private void createContractChart(Project currentProject) {
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
            for (ContractConsultant contractConsultant : mainContract.getContractConsultants()) {
                xAxis.addCategory(contractConsultant.getUser().getUsername());
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
            for (ContractConsultant contractConsultant : mainContract.getContractConsultants()) {
                DataSeries ls = new DataSeries(contractConsultant.getUser().getUsername());
                DataSeriesItem dataSeriesItem = new DataSeriesItem();
                dataSeriesItem.setName(contractConsultant.getUser().getUsername());
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

    private void createConsultantList(Project currentProject) {
        consultantsLayout.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        consultantsLayout.addComponent(responsiveLayout);
        ResponsiveRow responsiveRow = responsiveLayout.addRow();

        for (Contract mainContract : currentProject.getContracts().stream().sorted(Comparator.comparing(Contract::getActiveTo).reversed()).collect(Collectors.toList())) {
            for (ContractConsultant contractConsultant : mainContract.getContractConsultants()) {
                ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
                consultantRowDesign.getLblName().setValue(contractConsultant.getUser().getFirstname() + " " + contractConsultant.getUser().getLastname());
                consultantRowDesign.getTxtRate().setValue(Math.round(contractConsultant.getRate()) + "");
                consultantRowDesign.getTxtRate().setReadOnly(true);
                consultantRowDesign.getTxtHours().setValue(Math.round(contractConsultant.getHours()) + "");
                consultantRowDesign.getTxtHours().setReadOnly(true);
                consultantRowDesign.getVlHours().setVisible(contractConsultant.getContract().getContractType().equals(ContractType.PERIOD));
                consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(contractConsultant.getUser(), false));
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

    private void updateTreeGrid(Project currentProject) {
        budgetCard.getContainer().removeAllComponents();
        if(currentProject.getContracts().stream().anyMatch(contract -> contract.getContractType().equals(ContractType.AMOUNT))) {
            grid = createGrid(currentProject);
            ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            ResponsiveRow row = responsiveLayout.addRow();
            row.addColumn().withDisplayRules(12, 12, 8, 8).withComponent(grid).setGrow(true);
            row.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(createUsedBudgetChart(currentProject));
            budgetCard.getContainer().addComponent(responsiveLayout);
        }
    }

    private Chart createUsedBudgetChart(Project currentProject) {
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
            for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                LocalDate budgetDate = startDate;
                while (budgetDate.isBefore(endDate)) {
                    final LocalDate filterDate = budgetDate;
                    BudgetNew budget = budgetNewRepository.findByMonthAndYearAndContractConsultantAndProject(filterDate.getMonthValue() - 1, filterDate.getYear(), contractConsultant, currentProject);
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

    private Grid createGrid(Project currentProject) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        if(currentProject.getContracts().size()==0) return new Grid();
        LocalDate endDate = currentProject.getContracts().stream().max(Comparator.comparing(Contract::getActiveTo)).get().getActiveTo();
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);

        grid = new Grid<>();

        grid.addColumn(BudgetRow::getUsername).setWidth(150).setCaption("Consultant").setId("name-column");
        grid.addColumn(budgetRow -> budgetRow.getContractConsultant().getContract().getName()).setWidth(100).setCaption("Contract").setId("contract-column");
        grid.setFrozenColumnCount(2);
        grid.setWidth("100%");
        grid.getEditor().setEnabled(true);

        List<BudgetRow> budgetRows = new ArrayList<>();

        for (Contract mainContract : currentProject.getContracts()) {
            if(mainContract.getActiveTo().isBefore(startDate)) continue;
            if(!mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) continue;
            for (ContractConsultant contractConsultant : mainContract.getContractConsultants()) {
                BudgetRow budgetRow = new BudgetRow(contractConsultant, (int)(monthsBetween+1));
                LocalDate budgetDate = startDate;

                int month = 0;
                while(budgetDate.isBefore(mainContract.getActiveTo())) {
                    final LocalDate filterDate = budgetDate;

                    BudgetNew budget = budgetNewRepository.findByMonthAndYearAndContractConsultantAndProject(filterDate.getMonthValue()-1, filterDate.getYear(), contractConsultant, currentProject);

                    if(budget != null) {
                        budgetRow.setMonth(month, (budget.getBudget() / contractConsultant.getRate())+"");
                    } else {
                        budgetNewRepository.save(new BudgetNew(filterDate.getMonthValue()-1, filterDate.getYear(), 0.0, contractConsultant, currentProject));
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
                if(budgetCountDate.isAfter(budgetRow.getContractConsultant().getContract().getActiveTo())) continue;
                if(budgetCountDate.isBefore(budgetRow.getContractConsultant().getContract().getActiveFrom())) continue;
                if(budgetString==null) budgetString = "0.0";
                BudgetNew budget = budgetNewRepository.findByMonthAndYearAndContractConsultantAndProject(
                        budgetCountDate.getMonthValue() - 1,
                        budgetCountDate.getYear(),
                        budgetRow.getContractConsultant(),
                        currentProject);
                //if(budget.getProject()==null) budget.setProject(currentProject);
                budget.setBudget(Double.parseDouble(budgetString) * NumberConverter.parseDouble(budgetRow.getRate()));
                budgetNewRepository.save(budget);
                budgetCountDate = budgetCountDate.plusMonths(1);
            }
            updateTreeGrid(currentProject);
        });
        grid.setItems(budgetRows);
        if(budgetRows.size() == 0) budgetCard.setVisible(false);

        return grid;
    }

    private void reloadGrid(Optional<Project> currentProject) {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        //currentProject = getSelProject().getValue();
        currentProject.ifPresent(this::createDetailLayout);
        //if(getSelProject().getSelectedItem().isPresent()) createDetailLayout(currentProject);
    }
}
