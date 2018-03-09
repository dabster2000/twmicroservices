package dk.trustworks.invoicewebui.bots;

import allbegray.slack.bot.SlackbotClient;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webhook.SlackWebhookClient;
import me.ramswaroop.jbot.core.slack.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class HalSlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(HalSlackBot.class);

    private SlackWebhookClient webhookClient;

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;

    private String slackbotUrl = "https://trustworks.slack.com/services/hooks/slackbot?token=xoxb-51526069511-NDQN3KpwJeDwMGLZ5bKH6lLu";
    private SlackbotClient slackbotClient;
    private SlackWebApiClient webApiClient;

    @PostConstruct
    public void invokeSlackWebhook() {
        //webhookClient = SlackClientFactory.createWebhookClient(slackIncomingWebhookUrl);
        //slackbotClient = SlackClientFactory.createSlackbotClient(slackbotUrl);
        //webApiClient = SlackClientFactory.createWebApiClient(slackToken);
        /*
        Payload payload = new Payload();
        payload.setText("test text");
        payload.setChannel("@hans");
        payload.setIcon_emoji(":octocat:");

        Attachment attachment = new Attachment();
        attachment.setTitle("test attachment title");
        attachment.setColor("good");
        attachment.setText("test attachment text");
        attachment.addField(new Field("test field title 1", "test field value 1"));
        attachment.addField(new Field("test field title 2", "test field value 2"));
        payload.addAttachment(attachment);

        webhookClient.post(payload);
*/
        //slackbotClient.post("@hans", "test message 2");


       // webApiClient.postMessage("U036JELTN", "Test", "hal", true);

    }


    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }


}
