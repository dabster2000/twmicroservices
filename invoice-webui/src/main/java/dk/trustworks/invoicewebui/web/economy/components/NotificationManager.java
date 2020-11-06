package dk.trustworks.invoicewebui.web.economy.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.NotificationType;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringComponent
@SpringUI
public class NotificationManager {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Autowired
    public NotificationManager(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Transactional
    public Card init() {
        Card card = new Card();
        card.getLblTitle().setValue("Create notification");

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);

        RichTextArea richTextArea = new RichTextArea();
        vl.addComponent(richTextArea);

        Button btnAdd = new Button("ADD");
        btnAdd.addClickListener(event -> {
            for (User user : userService.findCurrentlyEmployedUsers(true)) {
                Notification notification = new Notification(user, LocalDate.now(), LocalDate.now().plusMonths(1), "Release note", richTextArea.getValue(), "", "", NotificationType.RELEASENOTE);
                notificationRepository.save(notification);
            }
            richTextArea.clear();
        });
        vl.addComponent(btnAdd);

        card.getContent().addComponent(vl);

        return card;
    }

}
