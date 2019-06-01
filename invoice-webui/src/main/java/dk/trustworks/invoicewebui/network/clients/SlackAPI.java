package dk.trustworks.invoicewebui.network.clients;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SlackAPI {

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;


    public void sendSlackMessage(User receiver, String message, Attachment... attachments) {
        SlackWebApiClient motherWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod(receiver.getSlackusername(), message);
        textMessage.setAs_user(true);
        textMessage.setAttachments(Arrays.asList(attachments));
        motherWebApiClient.postMessage(textMessage);
    }

}
