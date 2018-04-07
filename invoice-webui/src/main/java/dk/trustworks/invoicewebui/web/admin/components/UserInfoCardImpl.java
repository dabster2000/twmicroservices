package dk.trustworks.invoicewebui.web.admin.components;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class UserInfoCardImpl extends UserInfoCardDesign {

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;

    @Autowired
    private UserRepository userRepository;

    private Binder<User> binder;
    private User user;

    public UserInfoCardImpl() {
        getBtnUpdate().addClickListener(event -> {
            try {
                System.out.println("Save user info = " + user);
                binder.writeBean(user);
                System.out.println("Save user info = " + user);
                userRepository.save(user);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO})
    public void init(String userUUID) {
        user = userRepository.findOne(userUUID);
        binder = new Binder<>();

        SlackWebApiClient slackWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);
        List<allbegray.slack.type.User> slackUserList = slackWebApiClient.getUserList();
        getCbSlackID().setItems(slackUserList);
        getCbSlackID().setItemCaptionGenerator(allbegray.slack.type.User::getName);

        binder.forField(getTxtFirstname()).bind(User::getFirstname, User::setFirstname);
        binder.forField(getTxtLastname()).bind(User::getLastname, User::setLastname);
        binder.forField(getTxtUsername()).bind(User::getUsername, User::setUsername);
        binder.forField(getTxtEmail()).bind(User::getEmail, User::setEmail);
        binder.forField(getCbSlackID()).bind(value -> slackUserList.stream().filter(p -> p.getId().equals(value.getSlackusername())).findFirst().get(), (user, slackUser) -> user.setSlackusername(slackUser.getId()));
        binder.forField(getCbActive()).bind(User::isActive, User::setActive);
        binder.forField(getDfBirthday()).bind(User::getBirthday, User::setBirthday);
        binder.readBean(this.user);
    }
}
