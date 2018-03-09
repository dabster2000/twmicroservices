package dk.trustworks.invoicewebui.bots.conversations;

import ai.api.model.AIResponse;
import dk.trustworks.invoicewebui.bots.conversations.model.ConversationState;

public interface Conversation {
    String execute(String text, ConversationState conversationState, AIResponse response);
}
