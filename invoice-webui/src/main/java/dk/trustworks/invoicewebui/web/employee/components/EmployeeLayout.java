package dk.trustworks.invoicewebui.web.employee.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.AmbitionCategory;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.dashboard.cards.BubblesCardImpl;
import dk.trustworks.invoicewebui.web.dashboard.cards.ConsultantAllocationCardImpl;
import dk.trustworks.invoicewebui.web.employee.components.charts.AmbitionSpiderChart;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import dk.trustworks.invoicewebui.web.profile.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;

import java.util.Optional;

@SpringComponent
@SpringUI
public class EmployeeLayout extends VerticalLayout {

    private final User user;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final AmbitionRepository ambitionRepository;

    private final CKOExpenseRepository ckoExpenseRepository;

    private final BubbleRepository bubbleRepository;

    private final BubbleMemberRepository bubbleMemberRepository;

    private final AmbitionSpiderChart ambitionSpiderChart;

    private final PhotoRepository photoRepository;

    private final PhotoService photoService;

    private ResponsiveRow baseContentRow;

    private ResponsiveRow buttonContentRow;

    private ResponsiveRow workContentRow;
    private ResponsiveRow knowContentRow;
    private ResponsiveRow docsContentRow;
    private ResponsiveRow purpContentRow;
    private ResponsiveRow budgContentRow;

    private UserMonthReportImpl monthReport;

    @Autowired
    public EmployeeLayout(ContractService contractService, BudgetNewRepository budgetNewRepository, AmbitionRepository ambitionRepository, CKOExpenseRepository ckoExpenseRepository, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, AmbitionSpiderChart ambitionSpiderChart, PhotoRepository photoRepository, PhotoService photoService, UserMonthReportImpl monthReport) {
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.ambitionRepository = ambitionRepository;
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.ambitionSpiderChart = ambitionSpiderChart;
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.monthReport = monthReport;
        user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        baseContentRow = responsiveLayout.addRow();
        buttonContentRow = responsiveLayout.addRow();
        workContentRow = responsiveLayout.addRow();
        purpContentRow = responsiveLayout.addRow();
        purpContentRow.setVisible(false);
        knowContentRow = responsiveLayout.addRow();
        knowContentRow.setVisible(false);
        budgContentRow = responsiveLayout.addRow();
        budgContentRow.setVisible(false);
        docsContentRow = responsiveLayout.addRow();
        docsContentRow.setVisible(false);
        addComponent(responsiveLayout);
        loadData();
    }

    private void loadData() {
        createMonthReportCard();
    }

    public EmployeeLayout init() {
        return this;
    }

    private void createMonthReportCard() {

        //Box box = new Box();
        //CssLayout cssLayout = new CssLayout();
        //cssLayout.addComponent();
        //cssLayout.addComponent(new UserDetailsCardDesign());
        //box.getContent().addComponent(cssLayout);

        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new PhotoUploader(user.getUuid(), 400, 400, "Upload a photograph of this employee:", PhotoUploader.Step.PHOTO, photoRepository).getUploader());
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new UserDetailsCardDesign());
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new KeyPurposeDesign());

        final Button btnWork = new MButton(MaterialIcons.WORK, "work", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top").withEnabled(false);
        final Button btnKnowledge = new MButton(VaadinIcons.ACADEMY_CAP, "knowledge", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnBudget = new MButton(MaterialIcons.SHOPPING_CART, "it budget", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnDocuments = new MButton(MaterialIcons.ARCHIVE, "documents", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnPurpose = new MButton(MaterialIcons.TRENDING_UP, "purpose", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");

        btnWork.addClickListener(event -> {
            setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, workContentRow);
        });
        btnKnowledge.addClickListener(event -> {
            setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, knowContentRow);
        });
        btnBudget.addClickListener(event -> {
            setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, budgContentRow);
        });
        btnDocuments.addClickListener(event -> {
            setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, docsContentRow);
        });
        btnPurpose.addClickListener(event -> {
            setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, purpContentRow);
        });

        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnWork);
        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnPurpose);
        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnKnowledge);
        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnBudget);
        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(btnDocuments);
        buttonContentRow.addColumn().withDisplayRules(12, 6, 2, 2).withComponent(new MButton().withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top"));
        workContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new ConsultantAllocationCardImpl(contractService, budgetNewRepository, 2, 6, "consultantAllocationCardDesign"));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new TouchBaseDesign());
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new SpeedDateDesign());
        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user, AmbitionCategory.DOMAIN));
        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user, AmbitionCategory.SKILL));
        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user, AmbitionCategory.SYSTEM));
        knowContentRow.addColumn().withDisplayRules(12, 12, 7, 7).withComponent(new CKOExpenseImpl(ckoExpenseRepository, VaadinSession.getCurrent().getAttribute(UserSession.class).getUser()));

        BubblesCardImpl bubblesCard = new BubblesCardImpl(bubbleRepository, bubbleMemberRepository, photoService, Optional.of(user));
        bubblesCard.getContentHolder().setHeight(200, Unit.PIXELS);

        knowContentRow.addColumn().withDisplayRules(12, 12, 5, 5).withComponent(bubblesCard);

        monthReport.init();
        workContentRow
                .addColumn()
                .withDisplayRules(12, 12, 5, 5)
                .withComponent(monthReport);
    }

    private void setNewButtonPressState(Button btnWork, Button btnKnowledge, Button btnBudget, Button btnDocuments, Button btnPurpose, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button btnWork, Button btnKnowledge, Button btnBudget, Button btnDocuments, Button btnPurpose) {
        btnWork.setEnabled(true);
        btnKnowledge.setEnabled(true);
        btnPurpose.setEnabled(true);
        btnBudget.setEnabled(true);
        btnDocuments.setEnabled(true);
    }

    private void hideAllDynamicRows() {
        workContentRow.setVisible(false);
        knowContentRow.setVisible(false);
        purpContentRow.setVisible(false);
        docsContentRow.setVisible(false);
        budgContentRow.setVisible(false);
    }
}
