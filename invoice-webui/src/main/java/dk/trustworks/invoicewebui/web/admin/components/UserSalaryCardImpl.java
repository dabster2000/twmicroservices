package dk.trustworks.invoicewebui.web.admin.components;

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

    private User user;
    private List<Salary> salaries;

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
            userService.deleteSalaries(user, getGridSalaries().getSelectedItems());
            getGridSalaries().setItems(userService.findByUUID(user.getUuid()).getSalaries());
        });
        getBtnAddSalary().addClickListener(event -> {
            userService.create(user, new Salary(getDfDate().getValue(), Integer.parseInt(getTxtSalary().getValue())));
            user = userService.findByUUID(user.getUuid());
            salaries = user.getSalaries();
            getGridSalaries().setItems(salaries);
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);
        user = userService.findByUUID(userUUID);
        salaries = user.getSalaries();
        getGridSalaries().setItems(salaries);
    }
}
