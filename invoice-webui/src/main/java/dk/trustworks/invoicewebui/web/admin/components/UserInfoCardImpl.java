package dk.trustworks.invoicewebui.web.admin.components;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersListRequest;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserAccount;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.network.rest.AccountingRestService;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class UserInfoCardImpl extends UserInfoCardDesign {

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;

    private final UserService userRepository;

    private final AccountingRestService accountingRestService;

    private Binder<User> binder;
    private User user;
    private UserAccount userAccount;

    public UserInfoCardImpl(UserService userRepository, AccountingRestService accountingRestService) {
        this.userRepository = userRepository;
        this.accountingRestService = accountingRestService;
        getBtnUpdate().addClickListener(event -> {
            try {
                binder.writeBean(user);
                if(userAccount!=null) accountingRestService.saveUserAccount(userAccount);
                userRepository.update(user);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        getTxtAccountNumber().addValueChangeListener(event -> {
            getLblAccountText().setValue("");
            userAccount = null;
            if(!event.getValue().isEmpty()) {
                verifyEconomicsAccount(Integer.parseInt(event.getValue()));
            }
        });
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO})
    public void init(String userUUID) {
        user = userRepository.findByUUID(userUUID, false);
        binder = new Binder<>();

        getLblAccountText().setCaption("Verified owner");
        getTxtAccountNumber().setValue("");
        userAccount = accountingRestService.findUserAccountByUseruuid(user.getUuid());
        verifyEconomicsAccount(userAccount!=null?userAccount.getAccount():-1);

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(motherSlackToken);

        try {
            List<com.slack.api.model.User> members = methods.usersList(UsersListRequest.builder().build()).getMembers();
            if(members != null) {
                getCbSlackID().setItems(members);
                binder.forField(getCbSlackID()).bind(value -> members.stream().filter(p -> p.getId().equals(value.getSlackusername())).findFirst().orElse(null), (user, slackUser) -> user.setSlackusername(slackUser.getId()));
            }
        } catch (SlackApiException | IOException e) {
            e.printStackTrace();
        }

        getCbGender().setItems("FEMALE", "MALE");

        getCbSlackID().setItemCaptionGenerator(com.slack.api.model.User::getName);

        binder.forField(getTxtFirstname()).bind(User::getFirstname, User::setFirstname);
        binder.forField(getTxtLastname()).bind(User::getLastname, User::setLastname);
        binder.forField(getCbGender()).bind(User::getGender, User::setGender);
        binder.forField(getTxtUsername()).bind(User::getUsername, User::setUsername);
        binder.forField(getTxtEmail()).bind(User::getEmail, User::setEmail);
        binder.forField(getCbActive()).bind(User::isActive, User::setActive);
        binder.forField(getDfBirthday()).bind(User::getBirthday, User::setBirthday);
        binder.forField(getCbHealthcare()).bind(User::isHealthcare, User::setHealthcare);
        binder.forField(getCbPension()).bind(User::isPension, User::setPension);
        binder.forField(getCbPhotoconcent()).bind(User::isPhotoconsent, User::setPhotoconsent);
        binder.forField(getTxtDefects()).bind(User::getDefects, User::setDefects);
        binder.forField(getTxtPensiondetails()).bind(User::getPensiondetails, User::setPensiondetails);
        binder.forField(getTxtOther()).bind(User::getOther, User::setOther);
        binder.forField(getTxtCpr()).bind(User::getCpr, User::setCpr);
        binder.forField(getTxtPhone()).bind(User::getPhone, User::setPhone);

        binder.readBean(this.user);
    }

    private void verifyEconomicsAccount(int accountNumber) {
        userAccount = accountingRestService.getUserAccountByAccountNumber(accountNumber);
        if(userAccount!=null) {
            userAccount.setUseruuid(user.getUuid());
            userAccount.setAccount(accountNumber);
            getLblAccountText().setValue(userAccount.getUsername());
            getTxtAccountNumber().setValue(accountNumber+"");
        } else {
            getLblAccountText().setValue("None");
            //getTxtAccountNumber().setValue("");
        }
    }
}
