package dk.trustworks.invoicewebui.web.admin.components;


import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.RichTextArea;
import dk.trustworks.invoicewebui.model.Note;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.NoteType;
import dk.trustworks.invoicewebui.repositories.KeyPurposeRepository;
import dk.trustworks.invoicewebui.repositories.NotesRepository;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@Service
public class NotesBox {

    @Autowired
    private NotesRepository notesRepository;

    public Box createNotesBox(User user) {
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

}
