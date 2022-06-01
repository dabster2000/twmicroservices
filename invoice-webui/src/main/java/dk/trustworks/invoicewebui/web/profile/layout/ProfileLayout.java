package dk.trustworks.invoicewebui.web.profile.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.common.ImageBox;
import dk.trustworks.invoicewebui.web.common.ImageBoxImpl;
import dk.trustworks.invoicewebui.web.profile.components.ProfileCanvas;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static dk.trustworks.invoicewebui.model.enums.ConsultantType.*;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ProfileLayout extends VerticalLayout {

    private final TeamRestService teamRestService;
    private final PhotoService photoService;
    private final UserService userService;
    private final ProfileCanvas profileCanvas;
    private final ResponsiveRow selectorRow;
    private final ResponsiveRow viewRow;

    public ProfileLayout(TeamRestService teamRestService, PhotoService photoService, UserService userService, ProfileCanvas profileCanvas) {
        this.teamRestService = teamRestService;
        this.photoService = photoService;
        this.userService = userService;
        this.profileCanvas = profileCanvas;

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        addComponent(responsiveLayout);

        selectorRow = responsiveLayout.addRow();
        viewRow = responsiveLayout.addRow();
    }

    public ProfileLayout init() {
        clearContent();
        selectorRow.addColumn().withDisplayRules(12, 12,12,12).withComponent(createSelectorRow(), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        createDefaultViewRow();
        return this;
    }

    private void clearContent() {
        selectorRow.removeAllComponents();
        viewRow.removeAllComponents();
        selectorRow.setVisible(false);
    }

    private Component createSelectorRow() {
        List<User> users = userService.findCurrentlyEmployedUsers(true, CONSULTANT, STAFF, STUDENT);
        ComboBox<User> userComboBox = new ComboBox<>("Select employee: ", users);
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        userComboBox.addValueChangeListener(event -> {
            createUserViewRow(userComboBox.getSelectedItem().get());
        });

        return new HorizontalLayout(new BoxImpl().instance(userComboBox));
    }

    private void createDefaultViewRow() {
        List<Team> allTeams = teamRestService.getAllTeams();

        for (Team team : allTeams) {
            if(team.isTeamleadbonus()) {
                createTeamBox(team);
            }
        }

        Team staffTeam = allTeams.stream().filter(t -> t.getUuid().equals("04cb7837-51ba-4e33-92d7-b1106d3867b8")).findFirst().get();
        createTeamBox(staffTeam);

        Team managementTeam = allTeams.stream().filter(t -> t.getUuid().equals("f6e80289-2604-4a16-bcff-ee72affa3745")).findFirst().get();
        createTeamBox(managementTeam);

        Team ownersTeam = allTeams.stream().filter(t -> t.getUuid().equals("f7602dd6-9daa-43cb-8712-e9b1b99dc3a9")).findFirst().get();
        createTeamBox(ownersTeam);
    }

    private void createTeamBox(Team team) {
        ResponsiveLayout l = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        l.setSizeFull();
        ImageBox box = new ImageBoxImpl().instance(photoService.getRelatedPhotoResource(team.getUuid()));

        box.getCardHolder().addComponent(new MVerticalLayout(l).withStyleName("v-scrollable").withHeight(300, PIXELS));
        //box.getCardHolder().setHeight(750, Unit.PIXELS);

        viewRow.addColumn().withDisplayRules(12,12,6,4).withComponent(box);

        ResponsiveRow row = l.addRow();

        //row.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel(""));
        //row.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel(team.getName()).withStyleName("bold"), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        //row.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel(""));

        for (User employee : teamRestService.getUsersByTeamByMonth(team.getUuid(), LocalDate.now().withDayOfMonth(1)).stream().sorted(Comparator.comparing(User::getLastname)).collect(Collectors.toList())) {
            Image memberImage = photoService.getRoundMemberImage(employee.getUuid(), false, 100, Unit.PERCENTAGE);
            memberImage.setDescription(employee.getFirstname()+" "+employee.getLastname());
            memberImage.addClickListener(event -> {
                createUserViewRow(employee);
            });
            row.addColumn().withDisplayRules(3, 3, 3, 3).withComponent(memberImage);
        }
    }

    private void createUserViewRow(User user) {
        selectorRow.setVisible(true);
        viewRow.removeAllComponents();
        viewRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(profileCanvas.init(user));
    }

}
