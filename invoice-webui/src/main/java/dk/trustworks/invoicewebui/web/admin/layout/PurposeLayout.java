package dk.trustworks.invoicewebui.web.admin.layout;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.KeyPurpose;
import dk.trustworks.invoicewebui.model.Note;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.NoteType;
import dk.trustworks.invoicewebui.repositories.KeyPurposeRepository;
import dk.trustworks.invoicewebui.repositories.NotesRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.admin.components.NotesBox;
import dk.trustworks.invoicewebui.web.admin.components.PurposeBox;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.common.ImageBoxImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.CONSULTANT;
import static dk.trustworks.invoicewebui.model.enums.ConsultantType.STAFF;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@SpringUI
@SpringComponent
public class PurposeLayout {

    @Autowired
    private UserService userService;

    @Autowired
    private PurposeBox purposeBox;

    @Autowired
    private NotesBox notesBox;

    public void createEmployeeLayout(final ResponsiveRow contentRow) {
        //new Box().getContent().addComponent(purposeBannerImage);
        contentRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(new ImageBoxImpl().instance(new ThemeResource("images/banners/purpose-banner.jpeg")));

        ResponsiveColumn userSelectColumn = contentRow.addColumn()
                .withDisplayRules(12, 12, 4, 4);

        ResponsiveColumn purpColumn = contentRow.addColumn()
                .withDisplayRules(12, 12, 4, 4);

        ResponsiveColumn noteColumn = contentRow.addColumn()
                .withDisplayRules(12, 12, 4, 4);

        ListSelect<User> userListSelect = new ListSelect<>();

        userListSelect.setRows(20);
        userListSelect.setCaption("Select consultant:");
        userListSelect.setWidth(100, Sizeable.Unit.PERCENTAGE);
        userListSelect.setItemCaptionGenerator(User::getUsername);
        List<User> userList = userService.findCurrentlyEmployedUsers(true, CONSULTANT, STAFF);
        System.out.println("userList.size() = " + userList.size());
        userListSelect.setItems(userList);

        userListSelect.addSelectionListener(event -> {
            purpColumn.setComponent(purposeBox.createPurposeBox(userListSelect.getSelectedItems().stream().findFirst().get()));
            noteColumn.setComponent(notesBox.createNotesBox(userListSelect.getSelectedItems().stream().findFirst().get()));

        });

        Box userSelectBox = new BoxImpl().instance(new MVerticalLayout(userListSelect).withWidth(100, Sizeable.Unit.PERCENTAGE));

        userSelectColumn.setComponent(userSelectBox);
    }


}
