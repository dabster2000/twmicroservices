package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TwinColSelect;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.RoleRepository;
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
    private UserService userRepository;

    // TODO: Migrate to UserService
    private RoleRepository roleRepository;

    public RoleCardImpl() {

    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN})
    public void init(String userUUID) {
        getContainer().removeAllComponents();
        User user = userRepository.findByUUID(userUUID);
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
            System.out.println("Save roles");
            Set<RoleType> roleTypeSet = event.getValue();
            System.out.println("Save user roles = " + user);
            List<Role> roleList = user.getRoleList();
            for (Role role : roleList) {
                System.out.println("Delete role = " + role);
                roleRepository.delete(role);
            }
            for (RoleType roleType : roleTypeSet) {
                System.out.println("Add roleType = " + roleType);
                Role role = new Role(user, roleType);
                //user.getRoleList().add(role);
                roleRepository.save(role);
            }
        });
    }
}
