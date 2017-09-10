package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.repositories.SalaryRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hans on 09/09/2017.
 */

@SpringUI
@SpringComponent
public class UserSalaryCardImpl extends UserSalaryCardDesign {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private UserRepository userRepository;

    public UserSalaryCardImpl() {
        this.setVisible(false);
    }

    @Transactional
    //@AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);
        System.out.println("UserSalaryCardImpl.init");
        System.out.println("uuid = [" + userUUID + "]");

        getGridSalaries().setItems(userRepository.findOne(userUUID).getSalaries());
    }
}
