package dk.trustworks.invoicewebui.network.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackAPI {

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;


    /*
    public void sendSlackMessage(User receiver, String message, Attachment... attachments) {
        SlackWebApiClient motherWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod(receiver.getSlackusername(), message);
        textMessage.setAs_user(true);
        textMessage.setAttachments(Arrays.asList(attachments));
        motherWebApiClient.postMessage(textMessage);
    }
     */

}
