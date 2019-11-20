package dk.trustworks.invoicewebui.services;

import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.web.model.CateringEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;

@Service
public class EmailSender {

    @Value("${intra:email:online}")
    private String online;

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

        Notification.show("Order for '"+cateringEntry.getName()+"' submitted", Notification.Type.TRAY_NOTIFICATION);

        message = new SimpleMailMessage();
        message.setFrom("hans.lassen@trustworks.dk");
        message.setTo("hans.lassen@trustworks.dk");
        message.setSubject("Bestilling af mødeforplejning");
        message.setText(cateringEntry.toString());
        emailSender.send(message);
    }

    public void sendBirthdayInvitation(String receiver, String name, String company, boolean isGoing) {
        System.out.println("EmailSender.sendBirthdayInvitation");
        System.out.println("receiver = [" + receiver + "], name = [" + name + "]");

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message1 = new MimeMessageHelper(mimeMessage);
            message1.setFrom(new InternetAddress("event@trustworks.dk", "Trustworks"));
            message1.setReplyTo(new InternetAddress("event@trustworks.dk", "Trustworks"));
            message1.setTo(receiver);
            if(isGoing) {
                message1.setSubject("Bekræftelse på din tilmelding");
                message1.setText(
                        "<h3>Bekr&aelig;ftelse p&aring; din tilmelding</h3>\n" +
                                "<p>Du har nu tilmeldt dig Inspirationseftermiddag.</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p><strong>Tid og sted<br /></strong>Den 9. januar 2020 - kl. 16:00-19:30</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p><strong>Adresse:<br /></strong>Amagertorv 29a, 3. sal<br />1160 K&oslash;benhavn K, Denmark</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p>Vi ser frem til at se dig.</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p>Mange hilsner fra</p>\n" +
                                "<p>Trustworks</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p>(Hvis du bliver forhindret i at deltage kan du sende afmelding ved at <a href=\"http://event.trustworks.dk\">klikke her</a>)</p>"
                        , true);
            } else {
                message1.setSubject("Bekræftelse på din afmelding");
                message1.setText(
                        "<h3>Bekr&aelig;ftelse p&aring; din afmelding</h3>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p>Vi har nu noteret os, at du ikke deltager i Trustworks Inspirationseftermiddag.</p>\n" +
                                "<p>Det er vi selvf&oslash;lgelig kede af.</p>\n" +
                                "<p>Vi h&aring;ber, at du har mulighed for at deltage en anden gang.</p>\n" +
                                "<p>&nbsp;</p>\n" +
                                "<p>Mange hilsner fra</p>\n" +
                                "<p>Trustworks</p>"
                        , true);
            }
        };

        emailSender.send(preparator);
    }

    public void sendResetPassword(User user, String uuid) {
        System.out.println("EmailSender.sendResetPassword");
        System.out.println("user = [" + user + "]");
        //if(Boolean.valueOf(online)) {
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
        //} else {
            System.out.println("goto http://localhost:8080/#!reset/" + uuid);
        //}
    }

}
