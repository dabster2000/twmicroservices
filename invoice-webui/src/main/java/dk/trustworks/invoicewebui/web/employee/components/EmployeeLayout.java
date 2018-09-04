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
import dk.trustworks.invoicewebui.web.common.BoxImpl;
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

    private User user;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final ConsultantRepository consultantRepository;

    private final KeyPurposeRepository keyPurposeRepository;

    private final CKOExpenseRepository ckoExpenseRepository;

    private final NotesRepository notesRepository;

    private final BubbleRepository bubbleRepository;

    private final BubbleMemberRepository bubbleMemberRepository;

    private final ReminderHistoryRepository reminderHistoryRepository;

    private final ReminderRepository reminderRepository;

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
    public EmployeeLayout(ContractService contractService, BudgetNewRepository budgetNewRepository, ConsultantRepository consultantRepository, KeyPurposeRepository keyPurposeRepository, CKOExpenseRepository ckoExpenseRepository, NotesRepository notesRepository, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, ReminderHistoryRepository reminderHistoryRepository, ReminderRepository reminderRepository, AmbitionSpiderChart ambitionSpiderChart, PhotoRepository photoRepository, PhotoService photoService, UserMonthReportImpl monthReport) {
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.consultantRepository = consultantRepository;
        this.keyPurposeRepository = keyPurposeRepository;
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.notesRepository = notesRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.reminderRepository = reminderRepository;
        this.ambitionSpiderChart = ambitionSpiderChart;
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.monthReport = monthReport;
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
        user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        //Box box = new Box();
        //CssLayout cssLayout = new CssLayout();
        //cssLayout.addComponent();
        //cssLayout.addComponent(new UserDetailsCardDesign());
        //box.getContent().addComponent(cssLayout);

        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new BoxImpl().instance(new PhotoUploader(user.getUuid(), 100, 100, 400, 400, "Upload a photograph of this employee:", PhotoUploader.Step.PHOTO, photoRepository).getUploader()));
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new UserDetailsCardDesign());
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new KeyPurposeImpl(user, keyPurposeRepository));

        final Button btnWork = new MButton(MaterialIcons.WORK, "work", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top").withEnabled(false);
        final Button btnKnowledge = new MButton(VaadinIcons.ACADEMY_CAP, "knowledge", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnBudget = new MButton(MaterialIcons.SHOPPING_CART, "it budget", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnDocuments = new MButton(MaterialIcons.ARCHIVE, "documents", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnPurpose = new MButton(MaterialIcons.TRENDING_UP, "purpose", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");

        btnWork.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, workContentRow));
        btnKnowledge.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, knowContentRow));
        btnBudget.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, budgContentRow));
        btnDocuments.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, docsContentRow));
        btnPurpose.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnBudget, btnDocuments, btnPurpose, event, purpContentRow));

        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnWork);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnPurpose);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnKnowledge);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnBudget);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnDocuments);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(new MButton().withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top"));
        workContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new ConsultantAllocationCardImpl(contractService, budgetNewRepository, 2, 6, "consultantAllocationCardDesign"));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new TouchBaseImpl(user, notesRepository, reminderRepository));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new SpeedDateImpl(user, reminderHistoryRepository, consultantRepository));

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
