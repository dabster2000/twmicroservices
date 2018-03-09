package dk.trustworks.invoicewebui.bots;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import dk.trustworks.invoicewebui.bots.conversations.Conversation;
import dk.trustworks.invoicewebui.bots.conversations.model.ConversationState;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MotherSlackBot extends Bot {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MotherSlackBot.class);

    @Value("${slackBotToken}")
    private String slackToken;

    @Value("${apiAiToken}")
    private String apiAiToken;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private UserRepository userRepository;

    private AIDataService dataService;

    private Map<String, ConversationState> conversationMap;

    public MotherSlackBot() {
        conversationMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        log.info("MotherSlackBot.init");
        AIConfiguration configuration = new AIConfiguration(apiAiToken);
        dataService = new AIDataService(configuration);
    }

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, pattern = "")
    public void onReceiveDM(WebSocketSession session, Event event) {
        log.info("MotherSlackBot.onReceiveDM");
        log.info("event.getText() = " + event.getText());

        conversationMap =
                conversationMap.entrySet()
                        .stream()
                        .filter(p -> p.getValue().isValid())
                        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        AIRequest request = new AIRequest(event.getText());
        log.debug("event.getUser().getId() = " + event.getUserId());
        if(event.getUserId().equals("U7GH6EM70")) return;
        User user = userRepository.findBySlackusername(event.getUserId());

        AIResponse response = null;
        try {
            response = dataService.request(request);
        } catch (AIServiceException e) {
            e.printStackTrace();
        }

        if (response.getStatus().getCode() == 200) {
            String intentName = response.getResult().getMetadata().getIntentName();
            if(intentName.equals("Default Fallback Intent") && !conversationMap.containsKey(user.getSlackusername())) {
                reply(session, event, new Message(response.getResult().getFulfillment().getSpeech()));
                return;
            }

            if(intentName.equals("Default Fallback Intent") && conversationMap.containsKey(user.getSlackusername())) {
                ConversationState conversationState = conversationMap.get(user.getSlackusername());
                Conversation conversation = (Conversation) appContext.getBean(conversationState.getType());
                reply(session, event, new Message(conversation.execute(event.getText(), conversationState, response)));
                return;
            }

            ConversationState conversationState = createConversation(intentName, user);
            if(conversationState!=null) {
                log.info("conversation started: " + conversationState.getType());
                conversationMap.put(user.getSlackusername(), conversationState);
                Conversation conversation = (Conversation) appContext.getBean(conversationState.getType());
                reply(session, event, new Message(conversation.execute(event.getText(), conversationState, response)));
            }
        } else {
            System.err.println(response.getStatus().getErrorDetails());
        }
    }

    private ConversationState createConversation(String intent, User user) {
        switch (intent) {
            case "changePassword":
                return new ConversationState(user);
            default:
                return null;
        }
    }
}
