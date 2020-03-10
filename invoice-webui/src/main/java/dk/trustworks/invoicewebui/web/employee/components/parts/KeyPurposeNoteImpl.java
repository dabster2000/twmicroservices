package dk.trustworks.invoicewebui.web.employee.components.parts;

import dk.trustworks.invoicewebui.model.Note;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import static java.util.Base64.getDecoder;

public class KeyPurposeNoteImpl extends KeyPurposeNoteDesign {

    public KeyPurposeNoteImpl(Note note) {
        getLblDate().setValue(note.getNotedate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
        getLblNotes().setValue(new String(getDecoder().decode(note.getContent()), StandardCharsets.UTF_8));
    }
}
