package dk.trustworks.invoicewebui.services;

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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("michala.christensen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);
    }



}
