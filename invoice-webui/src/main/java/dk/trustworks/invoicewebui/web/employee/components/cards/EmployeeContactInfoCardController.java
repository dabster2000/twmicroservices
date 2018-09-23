package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserContactinfo;
import dk.trustworks.invoicewebui.repositories.UserContactinfoRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.profile.components.UserDetailsCardDesign;

@SpringUI
@SpringComponent
public class EmployeeContactInfoCardController {

    private final UserRepository userRepository;

    private final UserContactinfoRepository userContactinfoRepository;

    private User user;

    public EmployeeContactInfoCardController(UserRepository userRepository, UserContactinfoRepository userContactinfoRepository) {
        this.userRepository = userRepository;
        this.userContactinfoRepository = userContactinfoRepository;
    }

    public Component getCard(User user) {
        //this.user = user;
        UserDetailsCardDesign userDetailsCard = new UserDetailsCardDesign();

        Binder<User> userBinder = new Binder<>();
        Binder<UserContactinfo> contactinfoBinder = new Binder<>();

        userDetailsCard.getTxtName().setEnabled(false);
        userDetailsCard.getTxtName().setValue(user.getFirstname()+" "+user.getLastname());

        userBinder.forField(userDetailsCard.getDfBirthday()).bind(User::getBirthday, User::setBirthday);

        UserContactinfo contactinfo = userContactinfoRepository.findFirstByUser(user).orElse(new UserContactinfo(user, "", "" ,""));
        contactinfoBinder.forField(userDetailsCard.getTxtCity()).bind(UserContactinfo::getCity, UserContactinfo::setCity);
        contactinfoBinder.forField(userDetailsCard.getTxtPostal()).bind(UserContactinfo::getPostalCode, UserContactinfo::setPostalCode);
        contactinfoBinder.forField(userDetailsCard.getTxtStreet()).bind(UserContactinfo::getStreetName, UserContactinfo::setStreetName);

        userBinder.readBean(user);
        contactinfoBinder.readBean(contactinfo);

        userDetailsCard.getBtnUpdate().addClickListener(event -> {
            try {
                userBinder.writeBean(user);
                contactinfoBinder.writeBean(contactinfo);

                userRepository.save(user);
                userContactinfoRepository.save(contactinfo);

                Notification.show("Contact information updated", Notification.Type.TRAY_NOTIFICATION);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        return userDetailsCard;
    }
}
