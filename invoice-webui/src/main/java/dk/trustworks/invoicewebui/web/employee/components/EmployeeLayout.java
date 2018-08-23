package dk.trustworks.invoicewebui.web.employee.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
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

    private ResponsiveRow contentRow;

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

        contentRow = responsiveLayout.addRow();
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

        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new PhotoUploader(user.getUuid(), 400, 400, "Upload a photograph of this employee:", PhotoUploader.Step.PHOTO, photoRepository).getUploader());
        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new UserDetailsCardDesign());
        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new KeyPurposeDesign());
        contentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new ConsultantAllocationCardImpl(contractService, budgetNewRepository, 2, 6, "consultantAllocationCardDesign"));
        contentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new TouchBaseDesign());
        contentRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(new SpeedDateDesign());
        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user));

        BubblesCardImpl bubblesCard = new BubblesCardImpl(bubbleRepository, bubbleMemberRepository, photoService, Optional.of(user));
        bubblesCard.getContentHolder().setHeight(200, Unit.PIXELS);

        contentRow.addColumn().withDisplayRules(12, 12, 5, 5).withComponent(bubblesCard);
        contentRow.addColumn().withDisplayRules(12, 12, 7, 7).withComponent(new CKOExpenseImpl(ckoExpenseRepository, VaadinSession.getCurrent().getAttribute(UserSession.class).getUser()));

        monthReport.init();
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 5, 5)
                .withComponent(monthReport);
    }
}
