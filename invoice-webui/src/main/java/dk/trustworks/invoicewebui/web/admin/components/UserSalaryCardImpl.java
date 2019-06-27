package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.SalaryRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
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
    private SalaryRepository salaryRepository;

    @Autowired
    private UserRepository userRepository;

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
            salaryRepository.delete(getGridSalaries().getSelectedItems());
            getGridSalaries().setItems(userRepository.findOne(user.getUuid()).getSalaries());
        });
        getBtnAddSalary().addClickListener(event -> {
            salaryRepository.save(new Salary(getDfDate().getValue(), Integer.parseInt(getTxtSalary().getValue())));
            user = userRepository.findOne(user.getUuid());
            salaries = user.getSalaries();
            getGridSalaries().setItems(salaries);
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
    public void init(String userUUID) {
        this.setVisible(true);
        System.out.println("UserSalaryCardImpl.init");
        System.out.println("uuid = [" + userUUID + "]");


        user = userRepository.findOne(userUUID);
        salaries = user.getSalaries();

        getGridSalaries().setItems(salaries);
    }
}
