package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TwinColSelect;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class RoleCardImpl extends RoleCardDesign {

    @Autowired
    private UserService userService;

    public RoleCardImpl() {

    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN})
    public void init(String userUUID) {
        getContainer().removeAllComponents();
        User user = userService.findByUUID(userUUID);
        List<RoleType> roleTypes = Arrays.asList(RoleType.values());

        RoleType[] currentRoleTypes = new RoleType[user.getRoleList().size()];
        int i = 0;
        for (Role role : user.getRoleList()) {
            currentRoleTypes[i++] = role.getRole();
        }

        TwinColSelect<RoleType> twinColSelect = new TwinColSelect<>();
        twinColSelect.setItems(roleTypes);
        twinColSelect.select(currentRoleTypes);
        twinColSelect.setRows(6);
        twinColSelect.setLeftColumnCaption("Available roles");
        twinColSelect.setRightColumnCaption("Active roles");
        getContainer().addComponent(twinColSelect);

        twinColSelect.addValueChangeListener(event -> {
            Set<RoleType> roleTypeSet = event.getValue();
            List<Role> roleList = user.getRoleList();
            userService.deleteRoles(user, roleList);
            for (RoleType roleType : roleTypeSet) {
                Role role = new Role(roleType);
                user.getRoleList().add(role);
                userService.create(user, role);
            }
        });
    }
}
