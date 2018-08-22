package dk.trustworks.invoicewebui.web.employee.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.AmbitionRepository;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.employee.components.charts.AmbitionSpiderChart;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@SpringUI
public class EmployeeLayout extends VerticalLayout {

    private final User user;

    private final AmbitionRepository ambitionRepository;

    private final CKOExpenseRepository ckoExpenseRepository;

    private final BubbleRepository bubbleRepository;

    private final BubbleMemberRepository bubbleMemberRepository;

    private final AmbitionSpiderChart ambitionSpiderChart;

    private final PhotoService photoService;

    private ResponsiveRow contentRow;

    private UserMonthReportImpl monthReport;

    @Autowired
    public EmployeeLayout(AmbitionRepository ambitionRepository, CKOExpenseRepository ckoExpenseRepository, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, AmbitionSpiderChart ambitionSpiderChart, PhotoService photoService, UserMonthReportImpl monthReport) {
        this.ambitionRepository = ambitionRepository;
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.ambitionSpiderChart = ambitionSpiderChart;
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
        /*
        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new UserDetailsCardDesign());
        contentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(ambitionSpiderChart.getOrganisationChart(user));

        BubblesCardImpl bubblesCard = new BubblesCardImpl(bubbleRepository, bubbleMemberRepository, photoService, Optional.of(user));
        bubblesCard.getContentHolder().setHeight(200, Unit.PIXELS);
        contentRow.addColumn().withDisplayRules(12, 12, 5, 5).withComponent(bubblesCard);

        contentRow.addColumn().withDisplayRules(12, 12, 7, 7).withComponent(new CKOExpenseImpl(ckoExpenseRepository, VaadinSession.getCurrent().getAttribute(UserSession.class).getUser()));
*/
        monthReport.init();
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 5, 5)
                .withComponent(monthReport);
    }
}
