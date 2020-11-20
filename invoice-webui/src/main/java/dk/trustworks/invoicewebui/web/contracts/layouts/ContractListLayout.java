package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ContractDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractSearchImpl;
import dk.trustworks.invoicewebui.web.contracts.components.NavigationBar;
import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@SpringComponent
@SpringUI
public class ContractListLayout extends VerticalLayout {


    private final ClientService clientService;

    private final ContractService contractService;

    private final ContractSearchImpl contractSearch;

    private final ContractDetailLayout contractDetailLayout;

    private final PhotoService photoService;

    private ResponsiveLayout contractResponsiveLayout;
    private ResponsiveRow contractRow;
    private ResponsiveRow errorRow;

    @Autowired
    public ContractListLayout(ClientService clientService, ContractService contractService, ContractSearchImpl contractSearch, ContractDetailLayout contractDetailLayout, PhotoService photoService) {
        this.clientService = clientService;
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
        createErrorBox();
        contractRow = contractResponsiveLayout.addRow();
        this.addComponent(contractResponsiveLayout);
        if(contractSearch.getSelClient().getOptionalValue().isPresent()) reloadContractView(contractSearch.getSelClient().getValue());
    }

    private void createErrorBox() {
        errorRow = contractResponsiveLayout.addRow();
        Card errorCard = new Card();
        errorCard.getContent().setHeight(450, Unit.PIXELS);
        errorCard.getContent().addStyleName("v-scrollable");
        errorCard.getLblTitle().setValue("Work registration errors");
        //errorCard.getHlTitleBar().addComponent(new MButton("load all", event -> createErrorContent(100)));
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
                contractSearch.getSelClient().setItems(clientService.findAll());
            } else {
                contractSearch.getSelClient().setItems(clientService.findByActiveTrue());
            }
        });
    }

    private void reloadContractView(Client client) {
        errorRow.setVisible(false);
        contractRow.removeAllComponents();
        createContractView(client);
        createNewContractButton(client);
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
                contractFormDesign.getTxtAmount().setVisible(contractFormDesign.getCbType().getValue().equals(ContractType.AMOUNT) || contractFormDesign.getCbType().getValue().equals(ContractType.SKI));
                contractFormDesign.getBtnCreate().setVisible(true);
                contractFormDesign.getBtnUpdate().setVisible(false);

                contractFormDesign.getBtnCreate().addClickListener(event3 -> {
                    Contract contract = new Contract(
                            contractFormDesign.getCbType().getValue(),
                            contractFormDesign.getCbStatus().getValue(),
                            contractFormDesign.getTxtNote().getValue(),
                            contractFormDesign.getTxtRefid().getValue(),
                            contractFormDesign.getDfFrom().getValue().withDayOfMonth(1),
                            contractFormDesign.getDfTo().getValue().withDayOfMonth(contractFormDesign.getDfTo().getValue().lengthOfMonth()),
                            NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()),
                            client);
                    contractService.createContract(contract);
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

    private void createContractView(Client client) {
        for (Contract contract : clientService.findOne(client.getUuid()).getContracts().stream().sorted(comparing(Contract::getActiveTo).reversed()).collect(Collectors.toList())) {
            ContractDesign contractDesign = new ContractDesign();

            if(contract.getName()==null || contract.getName().equals("")) {
                try {
                    if(contract.getParentuuid()==null || contract.getParentuuid().equals("")) contract.setName(RandomWord.getNewWord(8));
                    else contract.setName(contractService.findOne(contract.getParentuuid()).getName());
                    contractService.updateContract(contract);
                } catch (WordLengthException e) {
                    Notification.show("Contract not valid. Contract already exists for the selected project and consultant in that period.", Notification.Type.ERROR_MESSAGE);
                }
            }

            contractDesign.getLblContractNumber().setValue(contract.getName());
            if(!(contract.getParentuuid()==null || contract.getParentuuid().equals(""))) {
                contractDesign.getLblTitle().setValue("Extended Contract");
                contractDesign.getBtnExtendContract().setEnabled(false);
                contractDesign.getBtnExtendContract().setVisible(false);
            }

            contractDesign.getLblType().setValue(contract.getContractType().name());

            contractDesign.getChkProjects().setItems(contractService.findProjectsByContractuuid(contract.getUuid()));
            contractDesign.getChkProjects().setItemCaptionGenerator(Project::getName);
            contractDesign.getChkProjects().setValue(new HashSet<>(contractService.findProjectsByContractuuid(contract.getUuid())));
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
                contractDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(contractConsultant.getUseruuid(), false));
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
                    contractService.deleteContract(contract.getUuid());
                    reloadContractView(clientService.findOne(client.getUuid()));
                }
            }));

            contractDesign.getBtnExtendContract().setIcon(MaterialIcons.PLAYLIST_ADD);
            contractDesign.getBtnExtendContract().addClickListener(event1 -> {
                Contract newContract = new Contract(contract);
                contractService.createContract(newContract);
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
    }

    // TODO: Create error list
    /*
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

     */
}
