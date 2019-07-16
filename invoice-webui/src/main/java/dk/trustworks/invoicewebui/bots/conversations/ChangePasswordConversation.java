package dk.trustworks.invoicewebui.bots.conversations;

import ai.api.model.AIResponse;
import dk.trustworks.invoicewebui.bots.conversations.model.ConversationState;
import dk.trustworks.invoicewebui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ChangePasswordConversation")
public class ChangePasswordConversation implements Conversation {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordConversation.class);

    private final UserService userService;

    @Autowired
    public ChangePasswordConversation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String text, ConversationState conversationState, AIResponse response) {
        switch (conversationState.getStep()) {
            case 0:
                conversationState.step++;
                return response.getResult().getFulfillment().getSpeech();
            case 1:
                conversationState.step++;
                log.debug("Change password...");
                conversationState.getUser().setPassword(text);
                userService.update(conversationState.getUser());
                conversationState.setValid(false);
                return "Password changed!";
            default:
                conversationState.setValid(false);
                return "Sorry - I'm confused!";
        }
    }
}
