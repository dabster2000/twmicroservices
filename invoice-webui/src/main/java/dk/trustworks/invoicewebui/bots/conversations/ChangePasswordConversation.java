package dk.trustworks.invoicewebui.bots.conversations;

import ai.api.model.AIResponse;
import dk.trustworks.invoicewebui.bots.conversations.model.ConversationState;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ChangePasswordConversation")
public class ChangePasswordConversation implements Conversation {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordConversation.class);

    private final UserRepository userRepository;

    @Autowired
    public ChangePasswordConversation(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                userRepository.save(conversationState.getUser());
                conversationState.setValid(false);
                return "Password changed!";
            default:
                conversationState.setValid(false);
                return "Sorry - I'm confused!";
        }
    }
}
