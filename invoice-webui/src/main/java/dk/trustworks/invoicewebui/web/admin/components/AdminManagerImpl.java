package dk.trustworks.invoicewebui.web.admin.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class AdminManagerImpl extends VerticalLayout {

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoCardImpl userInfoCard;

    @Autowired
    private UserSalaryCardImpl userSalaryCard;

    @Autowired
    private RoleCardImpl roleCard;

    ResponsiveLayout responsiveLayout;
    ComboBox<User> userComboBox;
    ResponsiveRow contentRow;

    public AdminManagerImpl() {
    }

    @Transactional
    @PostConstruct
    public void init() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        userComboBox = new ComboBox<>();
        userComboBox.setItems(userRepository.findAll());
        userComboBox.setItemCaptionGenerator(User::getUsername);
        responsiveLayout.addRow()
                .addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 4)
                .withComponent(userComboBox);
        userComboBox.addValueChangeListener(event -> {
            contentRow.removeAllComponents();
            loadData();
        });
        contentRow = responsiveLayout.addRow();
        addComponent(responsiveLayout);
    }

    private void loadData() {
        if(authorizer.hasAccess(UserInfoCardImpl.class)) createUserInfoCard();
        createUserSalaryCard();
        if(authorizer.hasAccess(RoleCardImpl.class)) createRoleCard();
    }

    private void createUserSalaryCard() {
        userSalaryCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userSalaryCard);
    }

    private void createUserInfoCard() {
        userInfoCard.init(userComboBox.getSelectedItem().get().getUuid());
        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(userInfoCard);
    }

    private void createRoleCard() {
        roleCard.init(userComboBox.getSelectedItem().get().getUuid());

        contentRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(roleCard);
    }
}
