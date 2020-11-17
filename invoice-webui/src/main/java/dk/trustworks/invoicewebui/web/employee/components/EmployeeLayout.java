package dk.trustworks.invoicewebui.web.employee.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.AmbitionCategory;
import dk.trustworks.invoicewebui.model.CkoCourseStudent;
import dk.trustworks.invoicewebui.model.Note;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.BudgetService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.common.ImageListItem;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.dashboard.cards.BubblesCardImpl;
import dk.trustworks.invoicewebui.web.dashboard.cards.ConsultantAllocationCardImpl;
import dk.trustworks.invoicewebui.web.employee.components.cards.AchievementCardController;
import dk.trustworks.invoicewebui.web.employee.components.cards.EmployeeContactInfoCardController;
import dk.trustworks.invoicewebui.web.employee.components.cards.KeyPurposeHeadlinesCardController;
import dk.trustworks.invoicewebui.web.employee.components.charts.AmbitionSpiderChart;
import dk.trustworks.invoicewebui.web.employee.components.charts.BillableConsultantHoursPerMonthChart;
import dk.trustworks.invoicewebui.web.employee.components.parts.CKOExpenseImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.KeyPurposeNoteImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.SpeedDateImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.TouchBaseImpl;
import dk.trustworks.invoicewebui.web.employee.components.tabs.DocumentTab;
import dk.trustworks.invoicewebui.web.employee.components.tabs.ItBudgetTab;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
@SpringUI
public class EmployeeLayout extends VerticalLayout {

    private User user;

    private final UserService userService;

    private final AvailabilityService availabilityService;

    private final KeyPurposeHeadlinesCardController keyPurposeHeadlinesCardController;

    private final AchievementCardController achievementCardController;

    private final CKOExpenseRepository ckoExpenseRepository;

    private final NotesRepository notesRepository;

    private final BubbleRepository bubbleRepository;

    private final BubbleMemberRepository bubbleMemberRepository;

    private final ReminderHistoryRepository reminderHistoryRepository;

    private final CKOCertificationsRepository ckoCertificationsRepository;

    private final ReminderRepository reminderRepository;

    private final AmbitionSpiderChart ambitionSpiderChart;

    private final AmbitionCategoryRepository ambitionCategoryRepository;

    private final MicroCourseStudentRepository microCourseStudentRepository;

    private final PhotoService photoService;

    private final BudgetService budgetService;

    private final BillableConsultantHoursPerMonthChart billableConsultantHoursPerMonthChart;

    private final ItBudgetTab itBudgetTab;

    private final DocumentTab documentTab;

    private final EmployeeContactInfoCardController employeeContactInfoCardController;

    private final ResponsiveRow baseContentRow;

    private final ResponsiveRow buttonContentRow;

    private final ResponsiveRow workContentRow;
    private final ResponsiveRow knowContentRow;
    private final ResponsiveRow skillContentRow;
    private final ResponsiveRow docsContentRow;
    private final ResponsiveRow purpContentRow;
    private final ResponsiveRow budgContentRow;

    private final UserMonthReportImpl monthReport;

    @Autowired
    public EmployeeLayout(UserService userService, AvailabilityService availabilityService, KeyPurposeHeadlinesCardController keyPurposeHeadlinesCardController, AchievementCardController achievementCardController, CKOExpenseRepository ckoExpenseRepository, NotesRepository notesRepository, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, ReminderHistoryRepository reminderHistoryRepository, CKOCertificationsRepository ckoCertificationsRepository, ReminderRepository reminderRepository, AmbitionSpiderChart ambitionSpiderChart, AmbitionCategoryRepository ambitionCategoryRepository, PhotoService photoService, BillableConsultantHoursPerMonthChart billableConsultantHoursPerMonthChart, MicroCourseStudentRepository microCourseStudentRepository, BudgetService budgetService, ItBudgetTab itBudgetTab, DocumentTab documentTab, EmployeeContactInfoCardController employeeContactInfoCardController, UserMonthReportImpl monthReport) {
        this.userService = userService;
        this.availabilityService = availabilityService;
        this.keyPurposeHeadlinesCardController = keyPurposeHeadlinesCardController;
        this.achievementCardController = achievementCardController;
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.notesRepository = notesRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.ckoCertificationsRepository = ckoCertificationsRepository;
        this.reminderRepository = reminderRepository;
        this.ambitionSpiderChart = ambitionSpiderChart;
        this.ambitionCategoryRepository = ambitionCategoryRepository;
        this.photoService = photoService;
        this.billableConsultantHoursPerMonthChart = billableConsultantHoursPerMonthChart;
        this.microCourseStudentRepository = microCourseStudentRepository;
        this.budgetService = budgetService;
        this.itBudgetTab = itBudgetTab;
        this.documentTab = documentTab;
        this.employeeContactInfoCardController = employeeContactInfoCardController;
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
        skillContentRow = responsiveLayout.addRow();
        skillContentRow.setVisible(false);
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

        baseContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(achievementCardController.getCard(user));

        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new BoxImpl().instance(new PhotoUploader(user.getUuid(), 100, 100, 400, 400, "Upload a photograph of this employee:", PhotoUploader.Step.PHOTO, photoService).getUploader()));
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(employeeContactInfoCardController.getCard(user));
        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(keyPurposeHeadlinesCardController.getCard(user));

        final Button btnWork = new MButton(MaterialIcons.WORK, "work", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top").withEnabled(false);
        final Button btnKnowledge = new MButton(VaadinIcons.ACADEMY_CAP, "knowledge", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnSkills = new MButton(VaadinIcons.ACADEMY_CAP, "skills", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnBudget = new MButton(MaterialIcons.SHOPPING_CART, "it budget", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnDocuments = new MButton(MaterialIcons.ARCHIVE, "documents", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");
        final Button btnPurpose = new MButton(MaterialIcons.TRENDING_UP, "purpose", event -> {}).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top");

        btnWork.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, workContentRow));
        btnKnowledge.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, knowContentRow));
        btnSkills.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, skillContentRow));
        btnBudget.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, budgContentRow));
        btnDocuments.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, docsContentRow));
        btnPurpose.addClickListener(event -> setNewButtonPressState(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose, event, purpContentRow));

        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnWork);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnPurpose);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnKnowledge);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnSkills);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnBudget);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnDocuments);
        //buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(new MButton().withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon","icon-align-top"));
        workContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new ConsultantAllocationCardImpl(availabilityService, budgetService, 2, 6, "consultantAllocationCardDesign"));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new TouchBaseImpl(user, notesRepository, reminderRepository));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new SpeedDateImpl(user, reminderHistoryRepository, userService));
        int adjustStartYear = 0;
        if(LocalDate.now().getMonthValue() <= 6)  adjustStartYear = 1;
        LocalDate localDateStart = LocalDate.now().withMonth(7).withDayOfMonth(1).minusYears(adjustStartYear);
        LocalDate localDateEnd = localDateStart.plusYears(1);
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new BoxImpl().instance(billableConsultantHoursPerMonthChart.createBillableConsultantHoursPerMonthChart(user, localDateStart, localDateEnd)));
        //workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new BoxImpl().instance(vacationPerYearChart.createExpensePerMonthChart(user)));
        workContentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(monthReport);

        for (Note note : notesRepository.findByUseruuidOrderByNotedateDesc(user.getUuid())) {
            purpContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new KeyPurposeNoteImpl(note));
        }

        budgContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(itBudgetTab.getTabLayout(user));
        docsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(documentTab.getTabLayout(user));

        ResponsiveRow skillRoleRow = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).addRow();
        ArrayList<String> skillRoles = new ArrayList<>();
        skillRoles.add("Business Architect");
        skillRoles.add("Solution Architect");
        skillRoles.add("Project Manager");
        skillRoles.add("Tender Consultant");
        skillRoles.add("UX");
        skillRoleRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel("<p>Udfyld kompetenceområderne nedenfor, så dine kollegaer kan se hvad du kan og salgsteamet kan se hvilke erfaringer de kan trække på.</p>" +
                "<p>Hvis du har lyst, kan du samtidig udfylde status for dine kompetence, hvor du angiver hvilke områder du ønsker at forbedre dig inden for, så har du en hurtig oversigt til dine KPC-samtaler og COOPS kan nemt se, hvor du passer perfekt til din næste kundeaftale.</p>").withContentMode(ContentMode.HTML).withFullWidth());

        BoxImpl skillRoleBox = new BoxImpl();
        skillRoleBox.getContent().addComponent(skillRoleRow);

        skillContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(skillRoleBox);
        for (AmbitionCategory ambitionCategory : ambitionCategoryRepository.findAll()) {
            skillContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user, ambitionCategory));
        }

        knowContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new CKOExpenseImpl(ckoExpenseRepository, ckoCertificationsRepository, VaadinSession.getCurrent().getAttribute(UserSession.class).getUser()));

        BubblesCardImpl bubblesCard = new BubblesCardImpl(bubbleRepository, bubbleMemberRepository, photoService, Optional.of(user));
        bubblesCard.getContentHolder().setHeight(200, Unit.PIXELS);

        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(bubblesCard);

        ImageCardDesign graduatedCoursesCard = getTrustworksAcademyCard("GRADUATED","TW Academy Courses","List of graduated courses...");
        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(graduatedCoursesCard);

        ImageCardDesign signedCoursesCard = getTrustworksAcademyCard("ENLISTED","TW Academy Courses","Queued for the following courses...");
        knowContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(signedCoursesCard);

        monthReport.init();
    }

    private ImageCardDesign getTrustworksAcademyCard(String type, String headline, String byline) {
        ImageCardDesign graduatedCoursesCard = new ImageCardDesign();
        graduatedCoursesCard.getImgTop().setSource(new ThemeResource("images/cards/knowledge/trustworks-academy.png"));
        graduatedCoursesCard.getVlContent().addComponent(
                new VerticalLayout(new MVerticalLayout(
                        new MLabel(headline).withStyleName("large bold"),
                        new MLabel(byline).withStyleName("small")
                ).withMargin(false))
        );

        for (CkoCourseStudent ckoCourseStudent : microCourseStudentRepository.findByUseruuid(user.getUuid()).stream().filter(ckoCourseStudent -> ckoCourseStudent.getStatus().equals(type)).collect(Collectors.toList())) {
            ImageListItem courseListItem = new ImageListItem().withComponents(photoService.getRelatedPhotoResource(ckoCourseStudent.getCkoCourse().getUuid()), ckoCourseStudent.getCkoCourse().getName(), ckoCourseStudent.getApplication().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
            graduatedCoursesCard.getVlContent().addComponent(courseListItem);
        }
        return graduatedCoursesCard;
    }

    private void setNewButtonPressState(Button btnWork, Button btnKnowledge, Button btnSkills, Button btnBudget, Button btnDocuments, Button btnPurpose, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button btnWork, Button btnKnowledge, Button btnSkills, Button btnBudget, Button btnDocuments, Button btnPurpose) {
        btnWork.setEnabled(true);
        btnKnowledge.setEnabled(true);
        btnSkills.setEnabled(true);
        btnPurpose.setEnabled(true);
        btnBudget.setEnabled(true);
        btnDocuments.setEnabled(true);
    }

    private void hideAllDynamicRows() {
        workContentRow.setVisible(false);
        knowContentRow.setVisible(false);
        skillContentRow.setVisible(false);
        purpContentRow.setVisible(false);
        docsContentRow.setVisible(false);
        budgContentRow.setVisible(false);
    }
}
