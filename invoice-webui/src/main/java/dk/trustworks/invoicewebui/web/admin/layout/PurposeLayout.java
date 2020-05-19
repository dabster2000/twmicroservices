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
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.common.ImageBoxImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.CONSULTANT;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@SpringUI
@SpringComponent
public class PurposeLayout {

    @Autowired
    private UserService userService;

    @Autowired
    private KeyPurposeRepository keyPurposeRepository;

    @Autowired
    private NotesRepository notesRepository;

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
        List<User> userList = userService.findCurrentlyEmployedUsers(CONSULTANT);
        System.out.println("userList.size() = " + userList.size());
        userListSelect.setItems(userList);

        userListSelect.addSelectionListener(event -> {
            purpColumn.setComponent(createPurposeBox(userListSelect.getSelectedItems().stream().findFirst().get()));
            noteColumn.setComponent(createNotesBox(userListSelect.getSelectedItems().stream().findFirst().get()));

        });

        Box userSelectBox = new BoxImpl().instance(new MVerticalLayout(userListSelect).withWidth(100, Sizeable.Unit.PERCENTAGE));

        userSelectColumn.setComponent(userSelectBox);
    }

    private Box createNotesBox(User user) {
        ComboBox<Note> noteComboBox = new ComboBox<>("Select note:");
        noteComboBox.setItemCaptionGenerator(note -> note.getNotedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        noteComboBox.setEmptySelectionCaption("create new note");
        noteComboBox.setEmptySelectionAllowed(true);

        DateField dateField = new DateField();
        dateField.setResolution(DateResolution.MONTH);
        dateField.setDateFormat("yyyy-MM-dd");
        dateField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        dateField.setValue(LocalDate.now());

        RichTextArea noteTextArea = new RichTextArea("Note:");
        noteTextArea.setWidth(100, Sizeable.Unit.PERCENTAGE);
        noteTextArea.setHeight(500, Sizeable.Unit.PIXELS);

        Button saveButton = new MButton("create", event -> {
            if(noteComboBox.getSelectedItem().isPresent()) {
                Note note = noteComboBox.getValue();
                note.setContent(getEncoder().encodeToString(noteTextArea.getValue().getBytes(StandardCharsets.UTF_8)));
                notesRepository.save(note);
                resetNoteForm(user, noteComboBox, dateField, noteTextArea);
            } else {
                Note note = new Note(user, NoteType.KEYPURPOSE, dateField.getValue(), getEncoder().encodeToString(noteTextArea.getValue().getBytes(StandardCharsets.UTF_8)));
                notesRepository.save(note);
                resetNoteForm(user, noteComboBox, dateField, noteTextArea);
            }
        }).withFullWidth();

        MButton deleteButton = new MButton("delete", event -> {
            notesRepository.delete(noteComboBox.getSelectedItem().get().getId());
            resetNoteForm(user, noteComboBox, dateField, noteTextArea);
        }).withFullWidth().withVisible(false);

        noteComboBox.addValueChangeListener(event -> {
            if(noteComboBox.getSelectedItem().isPresent()) {
                dateField.setVisible(false);
                noteTextArea.setValue(new String(getDecoder().decode(event.getSource().getValue().getContent()), StandardCharsets.UTF_8));
                saveButton.setCaption("update");
                deleteButton.setVisible(true);
            } else {
                dateField.setVisible(true);
                dateField.setValue(LocalDate.now());
                noteTextArea.setValue("");
                saveButton.setCaption("create");
                deleteButton.setVisible(false);
            }
        });

        noteComboBox.setItems(notesRepository.findByUseruuidOrderByNotedateDesc(user.getUuid()));

        return new BoxImpl().instance(new MVerticalLayout(noteComboBox, dateField, noteTextArea, saveButton, deleteButton).withWidth(100, Sizeable.Unit.PERCENTAGE));
    }

    private void resetNoteForm(User user, ComboBox<Note> noteComboBox, DateField dateField, RichTextArea noteTextArea) {
        noteComboBox.setValue(null);
        noteComboBox.setItems(notesRepository.findByUseruuidOrderByNotedateDesc(user.getUuid()));
        noteTextArea.setValue("");
        dateField.setValue(LocalDate.now());
    }

    private Box createPurposeBox(User user) {
        TextArea purp1 = new TextArea("Key Purpose 1:");
        purp1.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp1.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 1);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 1, ""));
            keyPurpose.setDescription(purp1.getValue());
            keyPurposeRepository.save(keyPurpose);
        });
        TextArea purp2 = new TextArea("Key Purpose 2:");
        purp2.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp2.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 2);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 2, ""));
            keyPurpose.setDescription(purp2.getValue());
            keyPurposeRepository.save(keyPurpose);
        });
        TextArea purp3 = new TextArea("Key Purpose 3:");
        purp3.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp3.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 3);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 3, ""));
            keyPurpose.setDescription(purp3.getValue());
            keyPurposeRepository.save(keyPurpose);
        });

        purp1.setValue("");
        purp2.setValue("");
        purp3.setValue("");
        List<KeyPurpose> keyPurposeList = keyPurposeRepository.findByUseruuidOrderByNumAsc(user.getUuid());
        if(keyPurposeList.size()>0) purp1.setValue(keyPurposeList.get(0).getDescription());
        if(keyPurposeList.size()>1) purp2.setValue(keyPurposeList.get(1).getDescription());
        if(keyPurposeList.size()>2) purp3.setValue(keyPurposeList.get(2).getDescription());

        Box purpBox = new BoxImpl().instance(new MVerticalLayout(purp1, purp2, purp3).withWidth(100, Sizeable.Unit.PERCENTAGE));
        return purpBox;
    }
}
