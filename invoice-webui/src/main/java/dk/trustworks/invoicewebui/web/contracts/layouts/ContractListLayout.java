package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.MainContract;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contracts.components.ContractDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractSearchImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@SpringComponent
@SpringUI
public class ContractListLayout extends VerticalLayout {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractSearchImpl contractSearch;

    @Autowired
    private ContractDetailLayout contractDetailLayout;

    private ResponsiveLayout contractResponsiveLayout;
    private ResponsiveRow contractRow;

    @PostConstruct
    public void init() {
        contractResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        contractResponsiveLayout.addRow().addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(contractSearch);
        contractSearch.getSelClient().setItemCaptionGenerator(Client::getName);

        contractRow = contractResponsiveLayout.addRow();

        contractSearch.getSelClient().addValueChangeListener(event -> {
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
