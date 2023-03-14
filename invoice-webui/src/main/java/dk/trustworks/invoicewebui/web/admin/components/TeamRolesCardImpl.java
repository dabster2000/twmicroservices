package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.data.Binder;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.TeamRole;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.TeamMemberType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.admin.model.TeamRolesGridItem;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 09/09/2017.
 */

@SpringUI
@SpringComponent
public class TeamRolesCardImpl extends TeamRolesCardDesign {

    private final TeamRestService teamRestService;
    private String useruuid;

    public TeamRolesCardImpl(TeamRestService teamRestService) {
        this.teamRestService = teamRestService;
        this.setVisible(false);

        getGridTeamRoles().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size() > 0) {
                getHlAddBar().setVisible(false);
                getBtnDelete().setVisible(true);
            } else {
                getHlAddBar().setVisible(true);
                getBtnDelete().setVisible(false);
            }
        });

        getBtnDelete().addClickListener(event -> {
            Set<TeamRolesGridItem> userTeamRoles = getGridTeamRoles().getSelectedItems();
            teamRestService.deleteTeamRoles(useruuid, userTeamRoles.stream().map(teamRolesGridItem -> new TeamRole(teamRolesGridItem.getUuid(), null, null, null, null, null)).collect(Collectors.toSet()));
            getGridTeamRoles().setItems(getTeamRolesGridItems(useruuid));
        });
        getBtnCreate().addClickListener(event -> {
            if(getCbTeam().getValue() == null) {
                Notification.show("Please select Team", Notification.Type.ERROR_MESSAGE);
                return;
            }
            if(getCbTeamRole().getValue() == null) {
                Notification.show("Please select Team Role", Notification.Type.ERROR_MESSAGE);
                return;
            }
            if(getDfStartDate().getValue() == null) {
                Notification.show("Please select Start Date", Notification.Type.ERROR_MESSAGE);
                return;
            }
            TeamRole teamRole = new TeamRole(UUID.randomUUID().toString(), getCbTeam().getValue().getUuid(), useruuid, getDfStartDate().getValue(), getDfEndDate().getValue(), getCbTeamRole().getValue());
            teamRestService.addUserToTeam(getCbTeam().getValue().getUuid(), teamRole);
            getGridTeamRoles().setItems(getTeamRolesGridItems(useruuid));
        });

        getCbTeam().setItems(teamRestService.getAllTeams());
        getCbTeam().setItemCaptionGenerator(Team::getName);
        getCbTeamRole().setItems(TeamMemberType.values());
        getCbTeamRole().setItemCaptionGenerator(TeamMemberType::name);

        Binder<TeamRolesGridItem> binder = getGridTeamRoles().getEditor().getBinder();
        Binder.Binding<TeamRolesGridItem, LocalDate> toDateBinding = binder.bind(getDfEndDate(), TeamRolesGridItem::getEnddate, TeamRolesGridItem::setEnddate);
        getGridTeamRoles().getColumns().get(3).setEditorBinding(toDateBinding);
        getGridTeamRoles().getEditor().setEnabled(true);
        getGridTeamRoles().getEditor().addSaveListener(event -> {
            teamRestService.deleteTeamRoles(event.getBean().getTeam(), Collections.singleton(new TeamRole(event.getBean().getUuid(), null, null, null, null, null)));
            TeamRole teamRole = new TeamRole(UUID.randomUUID().toString(), event.getBean().getUuid(), useruuid, getDfStartDate().getValue(), getDfEndDate().getValue(), getCbTeamRole().getValue());
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN})
    public void init(String useruuid) {
        this.setVisible(true);
        this.useruuid = useruuid;

        getTeamRolesGridItemList(useruuid);
    }

    private void getTeamRolesGridItemList(String useruuid) {
        List<TeamRolesGridItem> teamRolesGridItemList = getTeamRolesGridItems(useruuid);
        getGridTeamRoles().setItems(teamRolesGridItemList);
    }

    private List<TeamRolesGridItem> getTeamRolesGridItems(String useruuid) {
        List<Team> allTeams = teamRestService.getAllTeams();
        List<TeamRolesGridItem> teamRolesGridItemList = new ArrayList<>();
        for (TeamRole userTeamRole : teamRestService.findUserTeamRoles(useruuid)) {
            teamRolesGridItemList.add(new TeamRolesGridItem(userTeamRole.getUuid(), allTeams.stream().filter(team -> team.getUuid().equals(userTeamRole.getTeamuuid())).findAny().get().getName(), userTeamRole.getStartdate(), userTeamRole.getEnddate(), userTeamRole.getTeammembertype()));
        }
        return teamRolesGridItemList;
    }
}
