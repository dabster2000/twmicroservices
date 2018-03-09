package dk.trustworks.invoicewebui.bots.conversations.model;

import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDateTime;

public class ConversationState {

    private final String type = "ChangePasswordConversation";
    private final LocalDateTime creation;
    private final User user;
    public int step;
    private boolean valid;

    public ConversationState(User user) {
        this.user = user;
        this.creation = LocalDateTime.now();
        step = 0;
        valid = true;
    }

    public boolean isValid() {
        if(!valid) return valid;
        return creation.isAfter(LocalDateTime.now().minusMinutes(1));
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public User getUser() {
        return user;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
