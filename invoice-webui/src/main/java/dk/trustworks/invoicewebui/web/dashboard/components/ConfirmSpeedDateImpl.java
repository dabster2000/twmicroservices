package dk.trustworks.invoicewebui.web.dashboard.components;

import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.ReminderHistory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.repositories.ReminderHistoryRepository;

import java.time.LocalDate;

public class ConfirmSpeedDateImpl extends ConfirmSpeedDateDesign {


    public ConfirmSpeedDateImpl(User user, User target, Window window, ReminderHistoryRepository reminderHistoryRepository) {
        createInvitationLabel(target);

        getBtnDidIt().addClickListener(event -> {
            reminderHistoryRepository.save(new ReminderHistory(ReminderType.SPEEDDATE, user, LocalDate.now(), target.getUuid()));
            window.close();
        });

        getBtnNope().addClickListener(event -> {
            ReminderHistory reminderHistory = reminderHistoryRepository.findFirstByUseruuidAndTargetuuidAndType(target.getUuid(), user.getUuid(), ReminderType.SPEEDDATE);
            reminderHistoryRepository.delete(reminderHistory.getId());
            window.close();
        });
    }
    private void createInvitationLabel(User target) {
        getLblDescription().setValue("Have you been on speed date with "+target.getFirstname() +" "+ target.getLastname().substring(0, 1)+"?");
    }
}
