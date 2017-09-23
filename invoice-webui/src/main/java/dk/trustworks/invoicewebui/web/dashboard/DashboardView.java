package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.dashboard.cards.*;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Projects nearly ending
     * Reminder til projektowners at en client ikke har et logo
     * Reminder til projektejer at projekt ikke bør være aktivt mere (3 måneder uden arbeje)
     * Reminder om at der ikke er en projektejer til sales team
     * Reminder til projektejer at budgettet er ved at løbe ud på projekt/konsulent
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
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private TaskworkerconstraintRepository taskworkerconstraintRepository;

    @Autowired
    private TrustworksEventRepository trustworksEventRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DropboxAPI dropboxAPI;

    @Transactional
    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        ResponsiveLayout board = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        board.setSizeFull();
        board.setScrollable(true);

        BirthdayCardImpl birthdayCard = new BirthdayCardImpl(trustworksEventRepository, 1, 6, "birthdayCard");
        //irthdayCard.setHeight("600px");
        PhotosCardImpl photoCard = new PhotosCardImpl(dropboxAPI, 4, 6, "photoCard");
        //photoCard.setHeight("500px");
        NewsImpl newsCard = new NewsImpl(userRepository, projectRepository, 1, 6, "newsCard");
        //newsCard.setHeight("600px");
        DnaCardImpl dnaCard = new DnaCardImpl(3, 4, "dnaCard");
        //dnaCard.setHeight("500px");

        ConsultantLocationCardImpl locationCardDesign = new ConsultantLocationCardImpl(projectRepository, 2, 8, "locationCardDesign");
        locationCardDesign.init();
        locationCardDesign.setWidth("100%");

        VideoCardImpl monthNewsCardDesign = new VideoCardImpl(1, 4 , "monthNewsCardDesign");
        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null,
                new ExternalResource("https://www.youtube-nocookie.com/embed/PWoyyHDRd7U?rel=0&amp;controls=0&amp;showinfo=0"));
        browser2.setHeight("400px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);
        monthNewsCardDesign.getLblTitle().setValue("State of Trustworks");

        createTopBoxes(board);

        List<Box> boxes = new ArrayList<>();
        boxes.add(birthdayCard);
        boxes.add(newsCard);
        boxes.add(photoCard);
        boxes.add(locationCardDesign);
        boxes.add(monthNewsCardDesign);
        boxes.add(dnaCard);

        createRows(board, boxes);

        mainTemplate.setMainContent(board, DashboardView.VIEW_ICON, DashboardView.MENU_NAME, "World of Trustworks", DashboardView.VIEW_BREADCRUMB);
    }

    private void createTopBoxes(ResponsiveLayout board) {
        TopCardDesign consultantsCard = new TopCardDesign();
        // TODO: Count instead of load: https://stackoverflow.com/questions/37569467/spring-data-jpa-get-the-values-of-a-non-entity-column-of-a-custom-native-query
        float goodPeopleNow = userStatusRepository.findAllActive().size();
        String date = LocalDate.now().minusYears(1).toString("yyyy-MM-dd");
        // TODO: Count instead of load
        float goodPeopleLastYear = userStatusRepository.findAllActiveByDate(date).size();
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        consultantsCard.getImgIcon().setSource(new ThemeResource("images/icons/ic_people_black_48dp_2x.png"));
        consultantsCard.getLblNumber().setValue(Math.round(goodPeopleNow)+"");
        consultantsCard.getLblTitle().setValue("Good People");
        consultantsCard.getLblSubtitle().setValue(percent + "% more than last year");
        consultantsCard.getCardHolder().addStyleName("medium-blue");
        ResponsiveRow row0 = board.addRow();
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard);

        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        Map<String, Project> currentProjectSet = new HashMap<>();
        Map<String, Project> noProjectSet = new HashMap<>();
        float billableHoursThisYear = 0f;
        for (Work work : workRepository.findByPeriod(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                billableHoursThisYear += work.getWorkduration();
                currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            } else {
                noProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }

        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        Map<String, Project> lastProjectSet = new HashMap<>();
        float billableHoursLastYear = 0f;
        for (Work work : workRepository.findByPeriod(lastStartDate.toString("yyyy-MM-dd"), lastEndDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                billableHoursLastYear += work.getWorkduration();
                lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
        float projectsThisYear = currentProjectSet.size();
        int projectsLastYear = lastProjectSet.size();
        int percentProjects = Math.round((projectsThisYear / projectsLastYear) * 100) - 100;
        int percentBillableHours = Math.round((billableHoursThisYear / billableHoursLastYear) * 100) - 100;
        String projectsMoreOrLess = "more";
        String hoursMoreOrLess = "more";
        if(percentProjects < 0) projectsMoreOrLess = "less";
        if(percentBillableHours < 0) hoursMoreOrLess = "less";
        percentProjects = Math.abs(percentProjects);
        TopCardDesign consultantsCard2 = new TopCardDesign();
        consultantsCard2.getImgIcon().setSource(new ThemeResource("images/icons/ic_date_range_48pt_2x.png"));
        consultantsCard2.getLblNumber().setValue(""+currentProjectSet.size());
        consultantsCard2.getLblTitle().setValue("Active Projects");
        consultantsCard2.getLblSubtitle().setValue(percentProjects+"% "+projectsMoreOrLess+" than last year");
        consultantsCard2.getCardHolder().addStyleName("dark-green");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard2);

        TopCardDesign consultantsCard3 = new TopCardDesign();
        consultantsCard3.getImgIcon().setSource(new ThemeResource("images/icons/ic_access_time_48pt_2x.png"));
        consultantsCard3.getLblNumber().setValue(""+billableHoursThisYear);
        consultantsCard3.getLblTitle().setValue("Billable Hours");
        consultantsCard3.getLblSubtitle().setValue(percentBillableHours+"% "+hoursMoreOrLess+" than last year");
        consultantsCard3.getCardHolder().addStyleName("orange");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard3);

        TopCardDesign consultantsCard4 = new TopCardDesign();
        consultantsCard4.getImgIcon().setSource(new ThemeResource("images/icons/ic_people_black_48dp_2x.png"));
        consultantsCard4.getLblNumber().setValue("0");
        consultantsCard4.getLblTitle().setValue("Trustworks Consultants");
        consultantsCard4.getLblSubtitle().setValue("10% more than last year");
        consultantsCard4.getCardHolder().addStyleName("dark-grey");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard4);
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
            responsiveRows[i] = board.addRow();
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
        //Authorizer.authorize(this);
    }

}