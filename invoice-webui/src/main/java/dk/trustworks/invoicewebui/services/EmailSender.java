package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.web.model.CateringEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Autowired
    public JavaMailSender emailSender;

    public void sendCateringOrder(CateringEntry cateringEntry) {
        System.out.println("EmailSender.sendCateringOrder");
        System.out.println("cateringEntry = [" + cateringEntry + "]");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("michala.christensen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);

        message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("hans.lassen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);
    }

    public void sendResetPassword(User user, String uuid) {
        System.out.println("EmailSender.sendResetPassword");
        System.out.println("user = [" + user + "]");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo(user.getEmail());
        message.setSubject("Reset af password");
        message.setText("http://intra.trustworks.dk/#!reset/"+uuid);
        emailSender.send(message);

        message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("hans.lassen@trustworks.dk");
        message.setSubject("Reset af password - "+user.getUsername());
        message.setText("");
        emailSender.send(message);
    }

}
