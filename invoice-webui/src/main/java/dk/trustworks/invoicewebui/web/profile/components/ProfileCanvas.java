package dk.trustworks.invoicewebui.web.profile.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.employee.components.cards.AchievementCardController;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ProfileCanvas extends VerticalLayout {

    private final AchievementCardController achievementCardController;
    private final PhotoService photoService;
    private final KnowledgeChart knowledgeChart;
    private final TeamRestService teamRestService;

    private ResponsiveRow baseContentRow;
    private ResponsiveRow buttonContentRow;
    private ResponsiveRow workContentRow;
    private ResponsiveRow knowContentRow;
    private ResponsiveRow docsContentRow;
    private ResponsiveRow purpContentRow;
    private ResponsiveRow budgContentRow;
    private User user;

    public ProfileCanvas(AchievementCardController achievementCardController, PhotoService photoService, KnowledgeChart knowledgeChart, TeamRestService teamRestService) {
        this.achievementCardController = achievementCardController;
        this.photoService = photoService;
        this.knowledgeChart = knowledgeChart;
        this.teamRestService = teamRestService;
    }

    public ProfileCanvas init(User user) {
        this.user = user;
        this.removeAllComponents();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        baseContentRow = responsiveLayout.addRow();
        buttonContentRow = responsiveLayout.addRow();
        workContentRow = responsiveLayout.addRow();
        purpContentRow = responsiveLayout.addRow();
        knowContentRow = responsiveLayout.addRow();
        budgContentRow = responsiveLayout.addRow();
        docsContentRow = responsiveLayout.addRow();
        addComponent(responsiveLayout);

        createBase();

        return this;
    }

    public void createBase() {
        baseContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(achievementCardController.getCard(user));

        Image image = new Image(null, photoService.getRelatedPhotoResource(user.getUuid()));
        image.setWidth(100, Unit.PERCENTAGE);
        image.setWidth(100, Unit.PERCENTAGE);

        String teamname = "";
        List<Team> memberList = teamRestService.findByRoles(user.getUuid(), LocalDate.now(), "MEMBER");
        List<Team> leaderList = teamRestService.findByRoles(user.getUuid(), LocalDate.now(), "LEADER");

        if(leaderList != null && leaderList.size() > 0) teamname = "Team leader of " + leaderList.get(0).getName();
        else if(memberList != null && memberList.size() > 0) teamname = "Member of " + memberList.get(0).getName();

        baseContentRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new BoxImpl().instance(new MVerticalLayout(
                image,
                new MLabel(" "),
                new MLabel(user.getFirstname() + " " + user.getLastname()),
                new MLabel(teamname),
                new MLabel(" "),
                new MLabel(DateUtils.stringIt(user.getBirthday())),
                new MLabel(" "),
                new MLabel(user.getEmail()),
                new MLabel(user.getPhone()))));
        baseContentRow.addColumn().withDisplayRules(12, 12, 8, 8).withComponent(new BoxImpl().instance(knowledgeChart.getChart(user)));


    }
}
