package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ContractDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractSearchImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@SpringComponent
@SpringUI
public class ContractListLayout extends VerticalLayout {

    private final ContractService contractService;

    private final ContractSearchImpl contractSearch;

    private final ContractDetailLayout contractDetailLayout;

    private final WorkRepository workRepository;

    private final PhotoService photoService;

    private ResponsiveLayout contractResponsiveLayout;
    private ResponsiveRow contractRow;
    private ResponsiveRow errorRow;
    private Card errorCard;
    private VerticalLayout errorList;

    @Autowired
    public ContractListLayout(ContractService contractService, ContractSearchImpl contractSearch, ContractDetailLayout contractDetailLayout, WorkRepository workRepository, PhotoService photoService) {
        this.contractService = contractService;
        this.contractSearch = contractSearch;
        this.contractDetailLayout = contractDetailLayout;
        this.workRepository = workRepository;
        this.photoService = photoService;
    }

    @PostConstruct
    public void init() {
        contractResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        contractResponsiveLayout.addRow().addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(contractSearch);
        contractSearch.getSelClient().setItemCaptionGenerator(Client::getName);

        errorRow = contractResponsiveLayout.addRow();
        errorCard = new Card();
        errorCard.getContent().setHeight(450, Unit.PIXELS);
        errorCard.addStyleName("v-scrollable");
        errorCard.getLblTitle().setValue("Work registration errors");

        errorCard.getContent().removeAllComponents();
        errorList = new VerticalLayout();
        errorList.addComponent(new MLabel("Work registrations have the following errors:").withStyleName("failure"));
        errorCard.getContent().addComponent(errorList);
        errorRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(errorCard);

        Map<String, String> errors = new HashMap<>();
        for (Work work : workRepository.findByPeriod("2014-01-01", "2020-01-01")) {
            if(contractService.findConsultantRateByWork(work)==null)
                errors.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(),
                        "There is no valid contract for " + work.getUser().getUsername() +
                                " work on " + work.getTask().getProject().getClient().getName() +"'s project " +
                                work.getTask().getProject().getName());
        }
        for (String error : errors.values()) {
            errorList.addComponent(new MLabel(error).withWidth(100, Unit.PERCENTAGE));
        }

        contractRow = contractResponsiveLayout.addRow();

        contractSearch.getSelClient().addValueChangeListener(event -> {
            errorRow.setVisible(false);
            contractRow.removeAllComponents();
            Client client = event.getValue();
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

                if(mainContract.getContractType().equals(ContractType.AMOUNT)) {
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

                contractRow.addColumn()
                        .withDisplayRules(12, 12, 4, 4)
                        .withComponent(contractDesign);
            }
            Button btnNewContract = new Button("New Contract");
            contractRow.addColumn()
                    .withComponent(btnNewContract)
                    .withDisplayRules(12, 12, 4, 4);

            btnNewContract.addClickListener(event1 -> {
                btnNewContract.setVisible(false);
                ContractFormDesign contractFormDesign = new ContractFormDesign();
                contractFormDesign.getCbType().setItems(ContractType.values());
                contractFormDesign.getCbType().addValueChangeListener(event2 -> {
                    contractFormDesign.getChkProjects().setVisible(false);
                    contractFormDesign.getChkProjects().setItems(client.getProjects());
                    contractFormDesign.getChkProjects().setItemCaptionGenerator(Project::getName);
                    contractFormDesign.getDfFrom().setVisible(true);
                    contractFormDesign.getDfTo().setVisible(true);
                    contractFormDesign.getBtnEdit().setVisible(false);
                    if(contractFormDesign.getCbType().getValue().equals(ContractType.AMOUNT)) {
                        contractFormDesign.getTxtAmount().setVisible(true);
                    } else {
                        contractFormDesign.getTxtAmount().setVisible(false);
                    }
                    contractFormDesign.getBtnCreate().setVisible(true);
                    contractFormDesign.getBtnUpdate().setVisible(false);

                    contractFormDesign.getBtnCreate().addClickListener(event3 -> {
                        MainContract mainContract = contractService.createContract(new MainContract(
                                contractFormDesign.getCbType().getValue(),
                                contractFormDesign.getDfFrom().getValue(),
                                contractFormDesign.getDfTo().getValue(),
                                NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()),
                                client));
                        this.removeComponent(contractResponsiveLayout);
                        contractResponsiveLayout = contractDetailLayout.loadContractDetails(mainContract);
                        this.addComponent(contractResponsiveLayout);
                    });

                });

                contractRow.addColumn()
                        .withDisplayRules(12, 12, 4, 4)
                        .withComponent(contractFormDesign);
            });
        });

        this.addComponent(contractResponsiveLayout);
    }

}
