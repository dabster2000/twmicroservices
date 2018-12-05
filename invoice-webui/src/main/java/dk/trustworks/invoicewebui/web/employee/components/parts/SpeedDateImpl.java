package dk.trustworks.invoicewebui.web.employee.components.parts;

import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.ReminderHistory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.ReminderHistoryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpeedDateImpl extends SpeedDateDesign {

    private final User user;
    private final ReminderHistoryRepository reminderHistoryRepository;
    private final ConsultantRepository consultantRepository;
    private String target;

    public SpeedDateImpl(User user, ReminderHistoryRepository reminderHistoryRepository, ConsultantRepository consultantRepository) {
        this.user = user;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.consultantRepository = consultantRepository;

        createInvitationLabel();

        getBtnDidIt().addClickListener(event -> {
            reminderHistoryRepository.save(new ReminderHistory(ReminderType.SPEEDDATE, user, LocalDate.now(), target));
            createInvitationLabel();
        });
    }
    private void createInvitationLabel() {
        List<ReminderHistory> reminderHistoryList = reminderHistoryRepository.findByTypeAndUserOrderByTransmissionDateDesc(ReminderType.SPEEDDATE, user);
        List<String> useruuidList = reminderHistoryList.stream().map(ReminderHistory::getTargetuuid).collect(Collectors.toList());
        useruuidList.add(user.getUuid());
        List<Consultant> allConsultants = consultantRepository.findByTypeAndStatus(ConsultantType.CONSULTANT, StatusType.ACTIVE);
        List<Consultant> proposedConsultants = allConsultants.stream().filter(consultant -> !useruuidList.contains(consultant.getUuid())).collect(Collectors.toList());
        Consultant consultant = proposedConsultants.get(new Random(System.currentTimeMillis()).nextInt(proposedConsultants.size()));
        target = consultant.getUuid();
        getLblDescription().setValue("Why not invite "+consultant.getFirstname() +" "+ consultant.getLastname().substring(0, 1)+". to a speed date?");
    }
}
