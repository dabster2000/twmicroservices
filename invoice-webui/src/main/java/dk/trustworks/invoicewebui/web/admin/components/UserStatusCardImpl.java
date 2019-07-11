package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */

@SpringUI
@SpringComponent
public class UserStatusCardImpl extends UserStatusCardDesign {

    @Autowired
    private UserService userService;

    private User user;
    private List<UserStatus> userStatusList;

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
            userService.deleteUserStatuses(user, getGridSalaries().getSelectedItems());
            getGridSalaries().setItems(userService.findByUUID(user.getUuid()).getStatuses());
        });
        getBtnCreate().addClickListener(event -> {
            userService.create(user, new UserStatus(getCbType().getValue(), getCbStatus().getValue(), getDfDate().getValue(), Integer.parseInt(getTxtAllocation().getValue())));
            user = userService.findByUUID(user.getUuid());
            userStatusList = user.getStatuses();
            getGridSalaries().setItems(userStatusList);
        });
        getCbStatus().setItems(StatusType.values());
        getCbType().setItems(ConsultantType.values());
        getCbStatus().setItemCaptionGenerator(StatusType::name);
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);

        user = userService.findByUUID(userUUID);
        userStatusList = user.getStatuses();

        getGridSalaries().setItems(userStatusList);
    }
}
