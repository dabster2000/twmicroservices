package dk.trustworks.invoicewebui.web.profile.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.profile.components.ProfileCanvas;

import java.util.List;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ProfileLayout extends VerticalLayout {

    private final UserService userService;
    private final ProfileCanvas profileCanvas;
    private final ResponsiveRow selectorRow;
    private final ResponsiveRow viewRow;

    public ProfileLayout(UserService userService, ProfileCanvas profileCanvas) {
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
        return this;
    }

    private void clearContent() {
        selectorRow.removeAllComponents();
        viewRow.removeAllComponents();
    }

    private Component createSelectorRow() {
        List<User> users = userService.findCurrentlyWorkingEmployees(ConsultantType.CONSULTANT, ConsultantType.STAFF);
        ComboBox<User> userComboBox = new ComboBox<>("Select employee: ", users);
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.setEmptySelectionAllowed(false);
        userComboBox.addValueChangeListener(event -> {
            viewRow.removeAllComponents();
            viewRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(profileCanvas.init(userComboBox.getSelectedItem().get()));
        });

        return new HorizontalLayout(new BoxImpl().instance(userComboBox));
    }

}
