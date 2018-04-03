package dk.trustworks.invoicewebui.bots;

import allbegray.slack.webhook.SlackWebhookClient;
import me.ramswaroop.jbot.core.slack.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class HalSlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(HalSlackBot.class);

    private SlackWebhookClient webhookClient;

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${halSlackBotToken}")
    private String slackToken;

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;


    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }


}
