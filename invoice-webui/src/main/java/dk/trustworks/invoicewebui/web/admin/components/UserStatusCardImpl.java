package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.SalaryRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.repositories.UserStatusRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
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
    private UserStatusRepository userStatusRepository;

    @Autowired
    private UserRepository userRepository;

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
            userStatusRepository.delete(getGridSalaries().getSelectedItems());
            getGridSalaries().setItems(userRepository.findOne(user.getUuid()).getStatuses());
        });
        getBtnCreate().addClickListener(event -> {
            userStatusRepository.save(new UserStatus(user, getCbStatus().getValue(), getDfDate().getValue(), Integer.parseInt(getTxtAllocation().getValue())));
            user = userRepository.findOne(user.getUuid());
            userStatusList = user.getStatuses();
            getGridSalaries().setItems(userStatusList);
        });
        getCbStatus().setItems(StatusType.values());
        getCbStatus().setItemCaptionGenerator(StatusType::name);
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        System.out.println("UserStatusCardImpl.init");
        this.setVisible(true);

        user = userRepository.findOne(userUUID);
        userStatusList = user.getStatuses();

        getGridSalaries().setItems(userStatusList);
    }
}
