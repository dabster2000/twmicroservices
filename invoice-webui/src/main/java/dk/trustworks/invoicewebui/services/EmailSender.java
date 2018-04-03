package dk.trustworks.invoicewebui.services;

import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.web.model.CateringEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Value("${intra:email:online}")
    private String online;

    @Autowired
    public JavaMailSender emailSender;

    @Async
    public void sendCateringOrder(CateringEntry cateringEntry) {
        System.out.println("EmailSender.sendCateringOrder");
        System.out.println("cateringEntry = [" + cateringEntry + "]");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("michala.christensen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);

        Notification.show("Order for '"+cateringEntry.getName()+"' submitted", Notification.Type.TRAY_NOTIFICATION);

        message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("hans.lassen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);
    }

    @Async
    public void sendResetPassword(User user, String uuid) {
        System.out.println("EmailSender.sendResetPassword");
        System.out.println("user = [" + user + "]");
        if(Boolean.valueOf(online)) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("hans.lassen@trustworks.dk");
            message.setTo(user.getEmail());
            message.setSubject("Reset af password");
            message.setText("http://intra.trustworks.dk/#!reset/" + uuid);
            emailSender.send(message);

            message = new SimpleMailMessage();
            message.setFrom("hans.lassen@trustworks.dk");
            message.setTo("hans.lassen@trustworks.dk");
            message.setSubject("Reset af password - " + user.getUsername());
            message.setText("");
            emailSender.send(message);
        } else {
            System.out.println("goto http://localhost:8080/#!reset/" + uuid);
        }
    }

}
