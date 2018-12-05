package dk.trustworks.invoicewebui.web.employee.components.parts;

import dk.trustworks.invoicewebui.model.Note;
import dk.trustworks.invoicewebui.model.Reminder;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.repositories.NotesRepository;
import dk.trustworks.invoicewebui.repositories.ReminderRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TouchBaseImpl extends TouchBaseDesign {

    public TouchBaseImpl(User user, NotesRepository notesRepository, ReminderRepository reminderRepository) {
        Reminder reminderType = reminderRepository.findFirstByType(ReminderType.TOUCHBASE);
        List<Note> notes = notesRepository.findByUserOrderByNotedateDesc(user);
        Note note = (notes.size()>0)?notes.get(0):null;

        if(note == null) {
            getLblNextDate().setValue("3 months after your onboarding");
        } else if (note.getNotedate().isBefore(LocalDate.now().minusMonths(reminderType.getInterval().getMonths()))) {
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")).toUpperCase();
        } else {
            getLblNextDate().setValue(note.getNotedate().plusMonths(reminderType.getInterval().getMonths()).format(DateTimeFormatter.ofPattern("MMMM yyyy")).toUpperCase());
        }
    }
}
