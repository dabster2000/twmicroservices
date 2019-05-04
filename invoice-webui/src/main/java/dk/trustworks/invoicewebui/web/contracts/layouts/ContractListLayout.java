package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ContractDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractSearchImpl;
import dk.trustworks.invoicewebui.web.contracts.components.NavigationBar;
import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.client.shared.Resolution;
import org.tltv.gantt.client.shared.Step;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@SpringComponent
@SpringUI
public class ContractListLayout extends VerticalLayout {


    private final ClientRepository clientRepository;

    private final ContractService contractService;

    private final ContractSearchImpl contractSearch;

    private final ContractDetailLayout contractDetailLayout;

    private final PhotoService photoService;

    private ResponsiveLayout contractResponsiveLayout;
    private ResponsiveRow contractRow;
    private ResponsiveRow errorRow;
    private ResponsiveRow ganttRow;
    private Card errorCard;

    LocalDate startDate;
    LocalDate endDate;

    @Autowired
    public ContractListLayout(ClientRepository clientRepository, ContractService contractService, ContractSearchImpl contractSearch, ContractDetailLayout contractDetailLayout, PhotoService photoService) {
        this.clientRepository = clientRepository;
        this.contractService = contractService;
        this.contractSearch = contractSearch;
        this.contractDetailLayout = contractDetailLayout;
        this.photoService = photoService;
    }

    @PostConstruct
    public void init() {
        createLayout();
    }

    private void createLayout() {
        this.removeAllComponents();
        contractResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        createSearchBar();
        showGantt();
        createErrorBox();
        contractRow = contractResponsiveLayout.addRow();
        this.addComponent(contractResponsiveLayout);
        if(contractSearch.getSelClient().getOptionalValue().isPresent()) reloadContractView(contractSearch.getSelClient().getValue());
    }

    private void createErrorBox() {
        errorRow = contractResponsiveLayout.addRow();
        errorCard = new Card();
        errorCard.getContent().setHeight(450, Unit.PIXELS);
        errorCard.getContent().addStyleName("v-scrollable");
        errorCard.getLblTitle().setValue("Work registration errors");
        errorCard.getHlTitleBar().addComponent(new MButton("load all", event -> createErrorContent(100)));

        createErrorContent(2);
    }

    private void createSearchBar() {
        OnOffSwitch withInactiveClientsSwitch = new OnOffSwitch(false);
        contractResponsiveLayout.addRow().addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(new HorizontalLayout(contractSearch, withInactiveClientsSwitch));
        contractSearch.getSelClient().setItemCaptionGenerator(Client::getName);

        contractSearch.getSelClient().addValueChangeListener(event -> reloadContractView(event.getValue()));

        withInactiveClientsSwitch.addValueChangeListener(event -> {
            if(event.getValue()) {
                contractSearch.getSelClient().setItems(clientRepository.findByOrderByName());
            } else {
                contractSearch.getSelClient().setItems(clientRepository.findByActiveTrueOrderByName());
            }
        });
    }

    private void reloadContractView(Client client) {
        errorRow.setVisible(false);
        ganttRow.setVisible(false);
        contractRow.removeAllComponents();
        client = createContractView(client);
        createNewContractButton(client);
    }

    private void showGantt() {
        ganttRow = contractResponsiveLayout.addRow();
        //errorRow.removeAllComponents();

        Card card = new Card();
        card.getLblTitle().setValue("Customer Timeline");
        ganttRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(card);

        Gantt gantt = new Gantt();
        card.getContent().addComponent(gantt);

        gantt.setWidth(100, Unit.PERCENTAGE);
        gantt.setHeight(500, Unit.PIXELS);
        gantt.setResizableSteps(false);
        gantt.setMovableSteps(false);
        gantt.setResolution(Resolution.Week);
        gantt.setMovableStepsBetweenRows(true);
        gantt.setShowCurrentTime(true);

        List<Client> clients = clientRepository.findByActiveTrueOrderByName();
        //List<Contract> contracts = client.getContracts();
        //List<Project> projects = client.getProjects();

        startDate = LocalDate.now();
        endDate = LocalDate.now().plusMonths(1);

        Map<String, Step> contractSet = new HashMap<>();
        Map<String, Step> projectSet = new HashMap<>();

        for (Client client : clients) {
            createClient(gantt, client);
        }

/*
        for (Contract contract : contracts) {
            createContract(gantt, contractSet, projectSet, contract);
        }
        */
/*
        for (Project project : projects) {
            createProject(gantt, project, contractSet, projectSet);
        }
*/

        gantt.setStartDate(DateUtils.convertLocalDateToDate(startDate));
        gantt.setEndDate(DateUtils.convertLocalDateToDate(endDate));

        //gantt.addClickListener((Gantt.ClickListener) event -> Notification.show("Clicked" + event.getStep().getCaption()));

        //gantt.addMoveListener((Gantt.MoveListener) event -> Notification.show("Moved " + event.getStep().getCaption()));

        //gantt.addResizeListener((Gantt.ResizeListener) event -> Notification.show("Resized " + event.getStep().getCaption()));
    }

    private void createClient(Gantt gantt, Client client) {
        Step projectStep = new Step(client.getName());

        Optional<Contract> firstContract = client.getContracts().stream().filter(contract -> contract.getActiveTo().isAfter(LocalDate.now())).min(Comparator.comparing(Contract::getActiveFrom));
        Optional<Contract> lastContract = client.getContracts().stream().max(Comparator.comparing(Contract::getActiveTo));
        if(!firstContract.isPresent() || !lastContract.isPresent()) return;
        if(lastContract.get().getActiveTo().isBefore(LocalDate.now())) return;
        //projectStep.setStartDate(DateUtils.convertLocalDateToDate((firstContract.get().getActiveFrom().isBefore(LocalDate.now()))?LocalDate.now():firstContract.get().getActiveFrom()));
        projectStep.setStartDate(DateUtils.convertLocalDateToDate(firstContract.get().getActiveFrom()));
        projectStep.setEndDate(DateUtils.convertLocalDateToDate(lastContract.get().getActiveTo()));

        projectStep.setBackgroundColor("5FA75C");


        //projectSet.put(project.getUuid(), projectStep);

        gantt.addStep(projectStep);

        for (Contract contract : client.getContracts()) {
            Step relatedContract = createContract(contract);
            if(relatedContract == null) continue;
            //projectStep.addStep(relatedContract);
            gantt.addStep(relatedContract);
        }
    }

    private Step createContract(Contract contract) {
        if(contract.getActiveTo().isBefore(LocalDate.now())) return null;

        if(contract.getActiveFrom().isBefore(startDate)) startDate = contract.getActiveFrom();
        if(contract.getActiveTo().isAfter(endDate)) endDate = contract.getActiveTo();

        String consultantNames = String.join(",", contract.getContractConsultants().stream().map(consultant -> consultant.getUser().getUsername()).collect(Collectors.toList()));
        Step contractStep = new Step(contract.getContractType().name() + "(" + consultantNames + ")");
        //Date contractStartDate = DateUtils.convertLocalDateToDate((contract.getActiveFrom().isBefore(LocalDate.now()))?LocalDate.now():contract.getActiveFrom());
        Date contractStartDate = DateUtils.convertLocalDateToDate(contract.getActiveFrom());
        Date contractEndDate = DateUtils.convertLocalDateToDate(contract.getActiveTo());
        contractStep.setStartDate(contractStartDate);
        contractStep.setEndDate(contractEndDate);

        contractStep.setDescription(consultantNames);
        contractStep.setBackgroundColor("F9D8B0");

        double amountUsedOnContract = contractService.findAmountUsedOnContract(contract);
        double percentage;
        if(contract.getContractType().equals(ContractType.AMOUNT)) {
            percentage = (amountUsedOnContract/contract.getAmount()) * 100.0;
        } else {
            long weeks = ChronoUnit.WEEKS.between(contract.getActiveFrom(), contract.getActiveTo());
            double amount = 0.0;
            for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                amount += contractConsultant.getRate() * contractConsultant.getHours() * weeks;
            }
            percentage = (amountUsedOnContract / amount) * 100.0;
        }
        contractStep.setShowProgress(true);
        contractStep.setProgress(percentage);

        return contractStep;
    }

    private void showGantt(Client client) {
        errorRow.removeAllComponents();

        Card card = new Card();
        card.getLblTitle().setValue("Customer Timeline");
        errorRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(card);

        Gantt gantt = new Gantt();
        card.getContent().addComponent(gantt);

        gantt.setWidth(100, Unit.PERCENTAGE);
        gantt.setHeight(500, Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);
        gantt.setResolution(Resolution.Week);
        gantt.setMovableStepsBetweenRows(true);

        List<Contract> contracts = client.getContracts();
        List<Project> projects = client.getProjects();

        startDate = LocalDate.now();
        endDate = LocalDate.now().plusMonths(1);

        Map<String, Step> contractSet = new HashMap<>();
        Map<String, Step> projectSet = new HashMap<>();

        for (Contract contract : contracts) {
            createContract(gantt, contractSet, projectSet, contract);
        }
/*
        for (Project project : projects) {
            createProject(gantt, project, contractSet, projectSet);
        }
*/

        gantt.setStartDate(startDate);
        gantt.setEndDate(endDate);

        gantt.addClickListener((Gantt.ClickListener) event -> Notification.show("Clicked" + event.getStep().getCaption()));

        gantt.addMoveListener((Gantt.MoveListener) event -> Notification.show("Moved " + event.getStep().getCaption()));

        gantt.addResizeListener((Gantt.ResizeListener) event -> Notification.show("Resized " + event.getStep().getCaption()));
    }

    private Step createContract(Gantt gantt, Map<String, Step> contractSet, Map<String, Step> projectSet, Contract contract) {
        //if(contractSet.containsKey(contract.getUuid())) return contractSet.get(contract.getUuid());
        if(contract.getActiveFrom().isBefore(startDate)) startDate = contract.getActiveFrom();
        if(contract.getActiveTo().isAfter(endDate)) endDate = contract.getActiveTo();

        String consultantNames = String.join(",", contract.getContractConsultants().stream().map(consultant -> consultant.getUser().getUsername()).collect(Collectors.toList()));
        Step contractStep = new Step(contract.getContractType().name() + "(" + consultantNames + ")");
        contractStep.setStartDate(Date.from(contract.getActiveFrom().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        contractStep.setEndDate(Date.from(contract.getActiveTo().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        contractStep.setDescription(consultantNames);
        contractStep.setBackgroundColor("FBB14D");
        gantt.addStep(contractStep);

        //contractSet.put(contract.getUuid(), contractStep);
/*
        for (Project project : contract.getProjects()) {
            Step relatedProject = createProject(gantt, project, contractSet, projectSet);
        }
        */
        return contractStep;
    }

    private Step createProject(Gantt gantt, Project project, Map<String, Step> contractSet, Map<String, Step> projectSet) {
        if(projectSet.containsKey(project.getUuid())) return projectSet.get(project.getUuid());

        Step projectStep = new Step(project.getName());

        projectStep.setStartDate(Date.from(project.getContracts().stream().min(Comparator.comparing(Contract::getActiveFrom)).get().getActiveFrom().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        projectStep.setEndDate(Date.from(project.getContracts().stream().max(Comparator.comparing(Contract::getActiveTo)).get().getActiveTo().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        //String consultantNames = String.join(",", contract.getConsultants().stream().map(consultant -> consultant.getUser().getUsername()).collect(Collectors.toList()));

        //projectStep.setDescription(consultantNames);
        projectStep.setBackgroundColor("8FA78A");

        projectSet.put(project.getUuid(), projectStep);

        gantt.addStep(projectStep);

        for (Contract contract : project.getContracts()) {
            Step relatedContract = createContract(gantt, contractSet, projectSet, contract);
            //projectStep.addSubStep(relatedContract);
            gantt.addStep(relatedContract);
        }

        return projectStep;
    }

    private void createNewContractButton(Client client) {
        Button btnNewContract = new Button("New Contract");
        CssLayout cssLayout = new CssLayout(btnNewContract);
        contractRow.addColumn()
                .withComponent(cssLayout)
                .withDisplayRules(12, 12, 4, 4);

        btnNewContract.addClickListener(event1 -> {
            btnNewContract.setVisible(false);
            ContractFormDesign contractFormDesign = new ContractFormDesign();
            contractFormDesign.getCbType().setItems(ContractType.values());
            contractFormDesign.getCbStatus().setDescription("<h2>Contract Type</h2>" +
                    "<ul>" +
                    "<li><b>INACTIVE:</b> The contract is not signed, budget will not be used in allocation charts and consultants can't register hours on this contract</li>" +
                    "<li><b>BUDGET:</b>  The contract is not signed, but budget is included in consultant allocation, but no hours can be registered</li>" +
                    "<li><b>TIME:</b> The contract is not signed, but budget is included in consultant allocation and hours may now be registered</li>" +
                    "<li><b>SIGNED:</b> Same as <b>TIME</b> except the contract has now been signed by the client</li>" +
                    "<li><b>CLOSED:</b> Same as <b>BUDGET</b> except the contract has now been signed by the client</li>" +
                    "</ul>", ContentMode.HTML);
            contractFormDesign.getCbType().setDescription("<h2>Contract Type</h2>" +
                    "<ul>" +
                    "<li><b>PERIOD:</b> Your contract does not have a fixed sum, but is valid through a period og time with one or more consultant</li>" +
                    "<li><b>AMOUNT:</b>  Your contract have a fixed sum with must be used in a fixed period by one or more consultants</li>" +
                    "<li><b>SKI 2018:</b> Same as <b>AMOUNT</b> but is regulated using SKI 2018 rules every year</li>" +
                    "</ul>", ContentMode.HTML);
            contractFormDesign.getTxtRefid().setDescription(
                    "<p><img src=\"VAADIN/themes/invoice/images/screens/invoice_contract_id.png\"/></p>" +
                    "<p>Customer contract reference used on the invoice</p>", ContentMode.HTML);
            contractFormDesign.getDfFrom().setDescription("Including selected month...");
            contractFormDesign.getDfTo().setDescription("Not included selected month...");
            contractFormDesign.getBtnCreate().setCaption("Create Contract");
            contractFormDesign.getCbType().addValueChangeListener(event2 -> {
                if(contractFormDesign.getCbType().getValue().equals(ContractType.AMOUNT) || contractFormDesign.getCbType().getValue().equals(ContractType.SKI)) {
                    contractFormDesign.getTxtAmount().setVisible(true);
                } else {
                    contractFormDesign.getTxtAmount().setVisible(false);
                }
                contractFormDesign.getBtnCreate().setVisible(true);
                contractFormDesign.getBtnUpdate().setVisible(false);

                contractFormDesign.getBtnCreate().addClickListener(event3 -> {
                    Contract contract = null;
                    try {
                        contract = contractService.createContract(new Contract(
                                contractFormDesign.getCbType().getValue(),
                                contractFormDesign.getCbStatus().getValue(),
                                contractFormDesign.getTxtNote().getValue(),
                                contractFormDesign.getTxtRefid().getValue(),
                                contractFormDesign.getDfFrom().getValue().withDayOfMonth(1),
                                contractFormDesign.getDfTo().getValue().withDayOfMonth(contractFormDesign.getDfTo().getValue().lengthOfMonth()),
                                NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()),
                                client));
                    } catch (ContractValidationException e) {
                        e.printStackTrace();
                    }
                    this.removeComponent(contractResponsiveLayout);
                    NavigationBar navigationBar = new NavigationBar();
                    navigationBar.getBtnBack().addClickListener(event -> this.createLayout());
                    contractResponsiveLayout = contractDetailLayout.loadContractDetails(contract, navigationBar);
                    this.addComponent(contractResponsiveLayout);
                    Notification.show("Create Project?",
                            "At this time you may want to create one or more projects, " +
                                    "unless you have created them in advance. A contract can be assigned " +
                                     "to one or more projects. Consultants can't register hours on a project " +
                                    "without an active contract.", Notification.Type.HUMANIZED_MESSAGE);
                });

            });
            contractFormDesign.getCbStatus().setItems(ContractStatus.values());
            cssLayout.removeComponent(btnNewContract);
            cssLayout.addComponent(contractFormDesign);
            /*
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 4, 4)
                    .withComponent(contractFormDesign);
                    */
        });
    }

    private Client createContractView(Client client) {
        for (Contract contract : clientRepository.findOne(client.getUuid()).getContracts().stream().sorted(comparing(Contract::getActiveTo).reversed()).collect(Collectors.toList())) {
            ContractDesign contractDesign = new ContractDesign();

            if(contract.getName()==null || contract.getName().equals("")) {
                try {
                    if(contract.getParentuuid()==null || contract.getParentuuid().equals("")) contract.setName(RandomWord.getNewWord(8));
                    else contract.setName(contractService.findOne(contract.getParentuuid()).getName());
                    contractService.updateContract(contract);
                } catch (WordLengthException | ContractValidationException e) {
                    e.printStackTrace();
                }
            }

            contractDesign.getLblContractNumber().setValue(contract.getName());
            if(!(contract.getParentuuid()==null || contract.getParentuuid().equals(""))) {
                contractDesign.getLblTitle().setValue("Extended Contract");
                contractDesign.getBtnExtendContract().setEnabled(false);
                contractDesign.getBtnExtendContract().setVisible(false);
            }

            contractDesign.getLblType().setValue(contract.getContractType().name());

            contractDesign.getChkProjects().setItems(contract.getProjects());
            contractDesign.getChkProjects().setItemCaptionGenerator(Project::getName);
            contractDesign.getChkProjects().setValue(contract.getProjects());
            contractDesign.getChkProjects().setVisible(true);
            contractDesign.getChkProjects().setEnabled(false);

            String contractPeriodFrom = contract.getActiveFrom().format(DateTimeFormatter.ofPattern("MMM yyyy"));
            LocalDate activeTo = contract.getActiveTo();

            String contractPeriodTo = activeTo.format(DateTimeFormatter.ofPattern("MMM yyyy"));

            contractDesign.getLblPeriod().setValue(contractPeriodFrom + " - " + contractPeriodTo);

            if(contract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI)) {
                contractDesign.getLblAmount().setValue(contract.getAmount()+" kr.");
            } else {
                contractDesign.getLblAmount().setValue("");
                contractDesign.getLblAmount().setCaption("");
            }

            for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                contractDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(contractConsultant.getUser(), false));
            }

            contractDesign.getBtnEdit().addClickListener(event3 -> {
                this.removeComponent(contractResponsiveLayout);
                NavigationBar navigationBar = new NavigationBar();
                navigationBar.getBtnBack().addClickListener(event -> this.createLayout());
                contractResponsiveLayout = contractDetailLayout.loadContractDetails(contract, navigationBar);
                this.addComponent(contractResponsiveLayout);
            });

            contractDesign.getBtnDelete().addClickListener(event1 -> ConfirmDialog.show(UI.getCurrent(), "Really delete contract?", dialog -> {
                if(dialog.isConfirmed()) {
                    contractService.deleteContract(contract);
                    reloadContractView(clientRepository.findOne(client.getUuid()));
                }
            }));

            contractDesign.getBtnExtendContract().setIcon(MaterialIcons.PLAYLIST_ADD);
            contractDesign.getBtnExtendContract().addClickListener(event1 -> {
                Contract newContract = null;
                try {
                    newContract = contractService.createContract(new Contract(contract));
                } catch (ContractValidationException e) {
                    e.printStackTrace();
                }
                this.removeComponent(contractResponsiveLayout);
                NavigationBar navigationBar = new NavigationBar();
                navigationBar.getBtnBack().addClickListener(event -> this.createLayout());
                contractResponsiveLayout = contractDetailLayout.loadContractDetails(newContract, navigationBar);
                this.addComponent(contractResponsiveLayout);
            });

            contractRow.addColumn()
                    .withDisplayRules(12, 12, 4, 4)
                    .withComponent(contractDesign);
        }
        return client;
    }

    private void createErrorContent(int months) {
        errorCard.getContent().removeAllComponents();
        VerticalLayout errorList = new VerticalLayout();
        errorList.addComponent(new MLabel("Work registrations have the following errors:").withStyleName("failure"));
        errorCard.getContent().addComponent(errorList);
        errorRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(errorCard);

        LocalDate errorDate = LocalDate.now().withDayOfMonth(1);
        Map<String, Work> errors = contractService.getWorkErrors(errorDate, months);
        for (String error : contractService.createErrorList(errors)) {
            errorList.addComponent(new MLabel(error)
                    .withWidth(100, Unit.PERCENTAGE));
        }
    }
}
