package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.DashboardPreloader;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.repositories.TrustworksEventRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.EmailSender;
import dk.trustworks.invoicewebui.web.dashboard.cards.*;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 12/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = DashboardView.VIEW_NAME)
public class DashboardView extends VerticalLayout implements View {

    private static final Logger logger = LoggerFactory.getLogger(DashboardView.class);
    /**
     * Templates
     * Apps i Trustworks
     * Reminder til projektowners at en client ikke har et logo
     * Reminder om at der ikke er en projektejer til sales team
     * Reminder til projektejer at budgettet er ved at løbe ud på projekt/konsulent
     * Employee page med cv, billede, etc.
     * Project siden skal indeholde en graf over budgetterne
     * Man skal kunne ændre på nyhederne/events
     * Brugerne skal have tilsendt nye passwords
     * Man skal have overblik over sit eget IT budget
     * Overblik over projekter
     */

    public static final String VIEW_NAME = "mainmenu";
    public static final String VIEW_BREADCRUMB = "Dashboard";
    public static final FontIcon VIEW_ICON = MaterialIcons.DASHBOARD;
    public static final String MENU_NAME = "Dashboard";

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TrustworksEventRepository trustworksEventRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardPreloader dashboardPreloader;

    @Autowired
    private DashboardBoxCreator dashboardBoxCreator;

    @Autowired
    private EmailSender emailSender;

    @Transactional
    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        ResponsiveLayout board = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        board.setSizeFull();
        board.setScrollable(true);

        //BirthdayCardImpl birthdayCard = new BirthdayCardImpl(trustworksEventRepository, 1, 6, "birthdayCard");
        PhotosCardImpl photoCard = new PhotosCardImpl(dashboardPreloader, 1, 6, "photoCard");
        NewsImpl newsCard = new NewsImpl(userRepository, newsRepository, 1, 12, "newsCard");
        DnaCardImpl dnaCard = new DnaCardImpl(10, 4, "dnaCard");
        CateringCardImpl cateringCard = new CateringCardImpl(userRepository.findByActiveTrueOrderByUsername(), emailSender,3, 4, "cateringCard");
        cateringCard.init();
        //ConsultantLocationCardImpl locationCardDesign = new ConsultantLocationCardImpl(projectRepository, photoRepository, 2, 6, "locationCardDesign");
        VideoCardImpl monthNewsCardDesign = new VideoCardImpl(2, 6 , "monthNewsCardDesign");
        VideoCardImpl tripVideosCardDesign = new VideoCardImpl(3, 6, "tripVideosCardDesign");
        //ProjectTimelineImpl projectTimeline = new ProjectTimelineImpl(projectRepository, 2, 6, "projectTimeline");

        //projectTimeline.init();

        //locationCardDesign.init();
        //locationCardDesign.setWidth("100%");

        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrustworksStatus()));
        browser2.setHeight("400px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);

        tripVideosCardDesign.setWidth("100%");
        BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrips()[0]));
        tripVideoBrowser.setHeight("400px");
        tripVideoBrowser.setWidth("100%");
        //tripVideosCardDesign.getLblTitle().setValue("Trustworks Travel Videos");
        tripVideosCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

        dnaCard.getBoxComponent().setHeight("600px");
        cateringCard.getBoxComponent().setHeight("600px");

        photoCard.loadPhoto();

        createTopBoxes(board);

        List<Box> boxes = new ArrayList<>();
        //boxes.add(birthdayCard);
        boxes.add(newsCard);
        boxes.add(photoCard);
        //boxes.add(locationCardDesign);
        boxes.add(cateringCard);
        boxes.add(monthNewsCardDesign);
        boxes.add(tripVideosCardDesign);
        boxes.add(dnaCard);
        //boxes.add(projectTimeline);
        //boxes.add(statusCard);

        //createRows(board, boxes);
        ResponsiveRow mainRow = board.addRow().withGrow(true);
        ResponsiveColumn mainComponentColumn = mainRow.addColumn().withDisplayRules(12, 12, 9, 9);
        ResponsiveColumn leftColumn = mainRow.addColumn().withDisplayRules(12, 12, 3, 3);
        leftColumn.withComponent(newsCard);

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        mainComponentColumn.withComponent(mainLayout);
        ResponsiveRow row1 = mainLayout.addRow().withGrow(true);
        //ResponsiveRow row1 = board.addRow().withGrow(true);
        //row1.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(newsCard);
        row1.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(photoCard);

        ResponsiveRow row2 = mainLayout.addRow().withGrow(true);
        row2.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(monthNewsCardDesign);
        row2.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(tripVideosCardDesign);

        ResponsiveRow row3 = mainLayout.addRow().withGrow(true);
        row3.addColumn().withDisplayRules(12, 12, 6, 4).withComponent(cateringCard);
        row3.addColumn().withDisplayRules(12, 12, 6, 4).withComponent(dnaCard);

        mainTemplate.setMainContent(board, DashboardView.VIEW_ICON, DashboardView.MENU_NAME, "World of Trustworks", DashboardView.VIEW_BREADCRUMB);

    }

    private void createTopBoxes(ResponsiveLayout board) {
        ResponsiveRow row0 = board.addRow();
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodPeopleBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createActiveProjectsBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createBillableHoursBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createConsultantsPerProjectBox()));
    }

    private void createRows(ResponsiveLayout board, List<Box> boxes) {
        boxes.sort(Comparator.comparing(Box::getPriority).thenComparing(Box::getBoxWidth, Comparator.reverseOrder()));

        int maxRows = 0;
        int[] rowSize = new int[10];
        Map<Box, Integer> boxIntegerMap = new HashMap<>();
        for (Box box : boxes) {
            for (int i = 0; i < rowSize.length; i++) {
                if(rowSize[i] + box.getBoxWidth() <= 12) {
                    boxIntegerMap.put(box, i);
                    rowSize[i] += box.getBoxWidth();
                    if(maxRows < i) maxRows = i;
                    break;
                }
            }
        }

        ResponsiveRow[] responsiveRows = new ResponsiveRow[maxRows+1];
        for (int i = 0; i < maxRows + 1; i++) {
            responsiveRows[i] = board.addRow().withGrow(true);
        }

        Map<Box, Integer> result = boxIntegerMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        for (Box box : result.keySet()) {
            Integer row = boxIntegerMap.get(box);
            int[] widths = new int[4];
            switch (box.getBoxWidth()) {
                case 3:
                    widths[0] = 3;
                    widths[1] = 4;
                    widths[2] = 6;
                    widths[3] = 12;
                    break;
                case 4:
                    widths[0] = 4;
                    widths[1] = 6;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 6:
                    widths[0] = 6;
                    widths[1] = 6;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 8:
                    widths[0] = 8;
                    widths[1] = 12;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 9:
                    widths[0] = 9;
                    widths[1] = 12;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 12:
                    widths[0] = 12;
                    widths[1] = 12;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
            }
            responsiveRows[0].addColumn()
                    .withDisplayRules(widths[3], widths[2], widths[1], widths[0])
                    .withComponent(box.getBoxComponent());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
