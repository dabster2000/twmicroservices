package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserContactinfo;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.employee.components.parts.UserDetailsCardDesign;

@SpringUI
@SpringComponent
public class EmployeeContactInfoCardController {

    private final UserService userService;

    public EmployeeContactInfoCardController(UserService userService) {
        this.userService = userService;
    }

    public Component getCard(User user) {
        //this.user = user;
        UserDetailsCardDesign userDetailsCard = new UserDetailsCardDesign();

        Binder<User> userBinder = new Binder<>();
        Binder<UserContactinfo> contactinfoBinder = new Binder<>();

        userDetailsCard.getTxtName().setEnabled(false);
        userDetailsCard.getTxtName().setValue(user.getFirstname()+" "+user.getLastname());

        userBinder.forField(userDetailsCard.getDfBirthday()).bind(User::getBirthday, User::setBirthday);

        final UserContactinfo contactinfo = userService.findUserContactinfo(user.getUuid());//user.getUserContactinfo()!=null?user.getUserContactinfo():new UserContactinfo("", "" ,"", "");//userContactinfoRepository.findFirstByUser(user).orElse(new UserContactinfo("", "" ,"", ""));
        //if(contactinfo==null) contactinfo = new UserContactinfo("", "" ,"", "");
        contactinfoBinder.forField(userDetailsCard.getTxtCity()).bind(UserContactinfo::getCity, UserContactinfo::setCity);
        contactinfoBinder.forField(userDetailsCard.getTxtPostal()).bind(UserContactinfo::getPostalcode, UserContactinfo::setPostalcode);
        contactinfoBinder.forField(userDetailsCard.getTxtStreet()).bind(UserContactinfo::getStreetname, UserContactinfo::setStreetname);
        contactinfoBinder.forField(userDetailsCard.getTxtPhone()).bind(UserContactinfo::getPhone, UserContactinfo::setPhone);

        userBinder.readBean(user);
        contactinfoBinder.readBean(contactinfo);

        userDetailsCard.getBtnUpdate().addClickListener(event -> {
            try {
                userBinder.writeBean(user);
                contactinfoBinder.writeBean(contactinfo);

                userService.updateBirthday(user);
                userService.updateUserContactinfo(user.getUuid(), contactinfo);

                Notification.show("Contact information updated", Notification.Type.TRAY_NOTIFICATION);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        return userDetailsCard;
    }
}
