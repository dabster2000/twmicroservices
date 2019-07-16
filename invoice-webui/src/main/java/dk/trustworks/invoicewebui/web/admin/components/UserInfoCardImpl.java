package dk.trustworks.invoicewebui.web.admin.components;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.webapi.SlackWebApiClient;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.UserService;
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
    private UserService userRepository;

    private Binder<User> binder;
    private User user;

    public UserInfoCardImpl() {
        getBtnUpdate().addClickListener(event -> {
            try {
                binder.writeBean(user);
                userRepository.update(user);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO})
    public void init(String userUUID) {
        user = userRepository.findByUUID(userUUID);
        binder = new Binder<>();

        SlackWebApiClient slackWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);

        try {
            List<allbegray.slack.type.User> slackUserList = slackWebApiClient.getUserList();
            getCbSlackID().setItems(slackUserList);
            binder.forField(getCbSlackID()).bind(value -> slackUserList.stream().filter(p -> p.getId().equals(value.getSlackusername())).findFirst().orElse(null), (user, slackUser) -> user.setSlackusername(slackUser.getId()));
        } catch (SlackResponseErrorException e) {
            e.printStackTrace();
        }
        getCbSlackID().setItemCaptionGenerator(allbegray.slack.type.User::getName);

        binder.forField(getTxtFirstname()).bind(User::getFirstname, User::setFirstname);
        binder.forField(getTxtLastname()).bind(User::getLastname, User::setLastname);
        binder.forField(getTxtUsername()).bind(User::getUsername, User::setUsername);
        binder.forField(getTxtEmail()).bind(User::getEmail, User::setEmail);
        binder.forField(getCbActive()).bind(User::isActive, User::setActive);
        binder.forField(getDfBirthday()).bind(User::getBirthday, User::setBirthday);
        binder.readBean(this.user);
    }
}
