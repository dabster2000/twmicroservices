package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by hans on 09/09/2017.
 */

@SpringUI
@SpringComponent
public class UserStatusCardImpl extends UserStatusCardDesign {

    @Autowired
    private UserService userService;
    private String useruuid;

    public UserStatusCardImpl() {
        this.setVisible(false);

        getGridSalaries().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size() > 0) {
                getHlAddBar().setVisible(false);
                getBtnDelete().setVisible(true);
            } else {
                getHlAddBar().setVisible(true);
                getBtnDelete().setVisible(false);
            }
        });

        getBtnDelete().addClickListener(event -> {
            Set<UserStatus> userStatuses = getGridSalaries().getSelectedItems();
            userService.deleteUserStatuses(useruuid, userStatuses);
            getGridSalaries().setItems(userService.findUserStatusList(useruuid));
        });
        getBtnCreate().addClickListener(event -> {
            UserStatus userStatus = new UserStatus(getCbType().getValue(), getCbStatus().getValue(), getDfDate().getValue(), Integer.parseInt(getTxtAllocation().getValue()));
            userService.create(useruuid, userStatus);
            getGridSalaries().setItems(userService.findUserStatusList(useruuid));
        });
        getDfDate().setResolution(DateResolution.MONTH);
        getCbStatus().setItems(StatusType.values());
        getCbType().setItems(ConsultantType.values());
        getCbStatus().setItemCaptionGenerator(StatusType::toString);
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);

        this.useruuid = userUUID;
        getGridSalaries().setItems(userService.findUserStatusList(userUUID));
    }
}
