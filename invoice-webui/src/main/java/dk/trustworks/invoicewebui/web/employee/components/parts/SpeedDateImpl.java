package dk.trustworks.invoicewebui.web.employee.components.parts;

import dk.trustworks.invoicewebui.model.ReminderHistory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.repositories.ReminderHistoryRepository;
import dk.trustworks.invoicewebui.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpeedDateImpl extends SpeedDateDesign {

    private final User user;
    private final ReminderHistoryRepository reminderHistoryRepository;
    private final UserService userService;
    private String target;

    public SpeedDateImpl(User user, ReminderHistoryRepository reminderHistoryRepository, UserService userService) {
        this.user = user;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.userService = userService;

        createInvitationLabel();

        getBtnDidIt().addClickListener(event -> {
            reminderHistoryRepository.save(new ReminderHistory(ReminderType.SPEEDDATE, user, LocalDate.now(), target));
            createInvitationLabel();
        });
    }
    private void createInvitationLabel() {
        List<ReminderHistory> reminderHistoryList = reminderHistoryRepository.findByTypeAndUseruuidOrderByTransmissionDateDesc(ReminderType.SPEEDDATE, user.getUuid());
        List<String> useruuidList = reminderHistoryList.stream().map(ReminderHistory::getTargetuuid).collect(Collectors.toList());
        useruuidList.add(user.getUuid());
        List<User> allConsultants = userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT);
        allConsultants.addAll(userService.findCurrentlyEmployedUsers(ConsultantType.STAFF));
        List<User> proposedConsultants = allConsultants.stream().filter(user -> !useruuidList.contains(user.getUuid())).collect(Collectors.toList());
        User consultant = proposedConsultants.get(new Random(System.currentTimeMillis()).nextInt(proposedConsultants.size()));
        target = consultant.getUuid();
        getLblDescription().setValue("Why not invite "+consultant.getFirstname() +" "+ consultant.getLastname().substring(0, 1)+". to a speed date?");
    }
}
