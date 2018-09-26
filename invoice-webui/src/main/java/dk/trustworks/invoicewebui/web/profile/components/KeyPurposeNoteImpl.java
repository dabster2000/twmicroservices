package dk.trustworks.invoicewebui.web.profile.components;

import dk.trustworks.invoicewebui.model.Note;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;

import static java.util.Base64.getDecoder;

public class KeyPurposeNoteImpl extends KeyPurposeNoteDesign {

    public KeyPurposeNoteImpl(Note note) {
        getLblDate().setValue(note.getNotedate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
        try {
            getLblNotes().setValue(new String(getDecoder().decode(note.getContent()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            getLblNotes().setValue("Note unreadably...");
        }
    }
}