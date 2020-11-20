package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.network.rest.BubbleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BubbleService {

    private final BubbleRestService bubbleRestService;

    @Autowired
    public BubbleService(BubbleRestService bubbleRestService) {
        this.bubbleRestService = bubbleRestService;
    }


    public List<Bubble> findBubblesByActiveTrueOrderByCreatedDesc() {
        return bubbleRestService.findBubblesByActiveTrueOrderByCreatedDesc();
    }

    public List<Bubble> findByUseruuid(String uuid) {
        return bubbleRestService.findByUseruuid(uuid);
    }

    public void addBubbleMember(String bubbleuuid, String useruuid) {
        bubbleRestService.addBubbleMember(bubbleuuid, useruuid);
    }

    public void removeFromBubble(String bubbleuuid, String useruuid) {
        bubbleRestService.removeBubbleMember(bubbleuuid, useruuid);
    }

    public void saveBubble(Bubble bubble) {
        bubbleRestService.saveBubble(bubble);
    }

    public void updateBubble(Bubble bubble) {
        bubbleRestService.updateBubble(bubble);
    }

    public void removeAllMembers(String bubbleuuid) {
        bubbleRestService.removeAllMembers(bubbleuuid);
    }
}
