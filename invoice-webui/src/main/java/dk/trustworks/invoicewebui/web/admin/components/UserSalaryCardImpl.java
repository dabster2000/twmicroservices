package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
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
public class UserSalaryCardImpl extends UserSalaryCardDesign {

    @Autowired
    private UserService userService;
    private String useruuid;

    public UserSalaryCardImpl() {
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
            userService.deleteSalaries(useruuid, getGridSalaries().getSelectedItems());
            getGridSalaries().setItems(userService.findUserSalaries(useruuid));
        });
        getBtnAddSalary().addClickListener(event -> {
            userService.create(useruuid, new Salary(getDfDate().getValue(), Integer.parseInt(getTxtSalary().getValue())));
            getGridSalaries().setItems(userService.findUserSalaries(useruuid));
        });
        getDfDate().setResolution(DateResolution.MONTH);
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);
        useruuid = userUUID;
        getGridSalaries().setItems(userService.findUserSalaries(useruuid));
    }
}
