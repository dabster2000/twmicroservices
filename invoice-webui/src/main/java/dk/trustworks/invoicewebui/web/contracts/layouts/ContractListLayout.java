package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ContractDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractSearchImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
    private Card errorCard;
    private VerticalLayout errorList;

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
        createErrorBox();
        contractRow = contractResponsiveLayout.addRow();
        this.addComponent(contractResponsiveLayout);
    }

    private void createErrorBox() {
        errorRow = contractResponsiveLayout.addRow();
        errorCard = new Card();
        errorCard.getContent().setHeight(450, Unit.PIXELS);
        errorCard.getContent().addStyleName("v-scrollable");
        errorCard.getLblTitle().setValue("Work registration errors");
        errorCard.getHlTitleBar().addComponent(new MButton("load all", event -> {
            createErrorContent(100);
        }));

        createErrorContent(2);
    }

    private void createSearchBar() {
        contractResponsiveLayout.addRow().addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(contractSearch);
        contractSearch.getSelClient().setItemCaptionGenerator(Client::getName);

        contractSearch.getSelClient().addValueChangeListener(event -> {
            reloadContractView(event.getValue());
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
        contractRow.addColumn()
                .withComponent(btnNewContract)
                .withDisplayRules(12, 12, 4, 4);

        btnNewContract.addClickListener(event1 -> {
            btnNewContract.setVisible(false);
            ContractFormDesign contractFormDesign = new ContractFormDesign();
            contractFormDesign.getCbType().setItems(ContractType.values());
            contractFormDesign.getCbType().addValueChangeListener(event2 -> {
                if(contractFormDesign.getCbType().getValue().equals(ContractType.AMOUNT) || contractFormDesign.getCbType().getValue().equals(ContractType.SKI)) {
                    contractFormDesign.getTxtAmount().setVisible(true);
                } else {
                    contractFormDesign.getTxtAmount().setVisible(false);
                }
                contractFormDesign.getBtnCreate().setVisible(true);
                contractFormDesign.getBtnUpdate().setVisible(false);

                contractFormDesign.getBtnCreate().addClickListener(event3 -> {
                    MainContract mainContract = contractService.createContract(new MainContract(
                            contractFormDesign.getCbType().getValue(),
                            contractFormDesign.getCbStatus().getValue(),
                            contractFormDesign.getTxtNote().getValue(),
                            contractFormDesign.getDfFrom().getValue(),
                            contractFormDesign.getDfTo().getValue(),
                            NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()),
                            client));
                    this.removeComponent(contractResponsiveLayout);
                    contractResponsiveLayout = contractDetailLayout.loadContractDetails(mainContract);
                    this.addComponent(contractResponsiveLayout);
                });

            });
            contractFormDesign.getCbStatus().setItems(ContractStatus.values());

            contractRow.addColumn()
                    .withDisplayRules(12, 12, 4, 4)
                    .withComponent(contractFormDesign);
        });
    }

    private Client createContractView(Client client) {
        for (MainContract mainContract : client.getMainContracts()) {
            ContractDesign contractDesign = new ContractDesign();

            contractDesign.getLblType().setValue(mainContract.getContractType().name());

            contractDesign.getChkProjects().setItems(mainContract.getProjects());
            contractDesign.getChkProjects().setItemCaptionGenerator(Project::getName);
            contractDesign.getChkProjects().setValue(mainContract.getProjects());
            contractDesign.getChkProjects().setVisible(true);
            contractDesign.getChkProjects().setEnabled(false);

            contractDesign.getLblPeriod().setValue(
                    mainContract.getActiveFrom().format(DateTimeFormatter.ofPattern("MMM yyyy")) + " - " +
                            mainContract.getActiveTo().format(DateTimeFormatter.ofPattern("MMM yyyy"))
            );

            if(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) {
                contractDesign.getLblAmount().setValue(mainContract.getAmount()+" kr.");
            } else {
                contractDesign.getLblAmount().setVisible(false);
            }

            for (Consultant consultant : mainContract.getConsultants()) {
                contractDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));
            }

            contractDesign.getBtnEdit().addClickListener(event3 -> {
                this.removeComponent(contractResponsiveLayout);
                contractResponsiveLayout = contractDetailLayout.loadContractDetails(mainContract);
                this.addComponent(contractResponsiveLayout);
            });

            contractDesign.getBtnDelete().addClickListener(event1 -> {
                ConfirmDialog.show(UI.getCurrent(), "Really delete contract?", dialog -> {
                    if(dialog.isConfirmed()) {
                        contractService.deleteContract(mainContract);
                        reloadContractView(clientRepository.findOne(client.getUuid()));
                    }
                });
            });

            contractRow.addColumn()
                    .withDisplayRules(12, 12, 4, 4)
                    .withComponent(contractDesign);
        }
        return client;
    }

    private void createErrorContent(int months) {
        errorCard.getContent().removeAllComponents();
        errorList = new VerticalLayout();
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
