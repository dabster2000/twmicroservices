package dk.trustworks.invoicewebui.web.profile.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.profile.components.ProfileCanvas;

import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.*;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ProfileLayout extends VerticalLayout {

    private final PhotoService photoService;
    private final UserService userService;
    private final ProfileCanvas profileCanvas;
    private final ResponsiveRow selectorRow;
    private final ResponsiveRow viewRow;

    public ProfileLayout(PhotoService photoService, UserService userService, ProfileCanvas profileCanvas) {
        this.photoService = photoService;
        this.userService = userService;
        this.profileCanvas = profileCanvas;

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        addComponent(responsiveLayout);

        selectorRow = responsiveLayout.addRow();
        viewRow = responsiveLayout.addRow();
    }

    public ProfileLayout init() {
        clearContent();
        selectorRow.addColumn().withDisplayRules(12, 12,12,12).withComponent(createSelectorRow(), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        createDefaultViewRow();
        return this;
    }

    private void clearContent() {
        selectorRow.removeAllComponents();
        viewRow.removeAllComponents();
        selectorRow.setVisible(false);
    }

    private Component createSelectorRow() {
        List<User> users = userService.findCurrentlyEmployedUsers(true, CONSULTANT, STAFF);
        ComboBox<User> userComboBox = new ComboBox<>("Select employee: ", users);
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        userComboBox.addValueChangeListener(event -> {
            createUserViewRow(userComboBox.getSelectedItem().get());
        });

        return new HorizontalLayout(new BoxImpl().instance(userComboBox));
    }

    private void createDefaultViewRow() {
        for (User employee : userService.findCurrentlyEmployedUsers(true, CONSULTANT, STAFF, STUDENT)) {
            Image memberImage = photoService.getRoundMemberImage(employee, false, 100, Unit.PERCENTAGE);
            memberImage.addClickListener(event -> {
                createUserViewRow(employee);
            });
            viewRow.addColumn().withDisplayRules(3, 2, 1, 1).withComponent(memberImage);
        }
    }

    private void createUserViewRow(User user) {
        selectorRow.setVisible(true);
        viewRow.removeAllComponents();
        viewRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(profileCanvas.init(user));
    }

}
