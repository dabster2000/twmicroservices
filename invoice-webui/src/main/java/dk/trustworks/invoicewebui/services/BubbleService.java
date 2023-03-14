package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.network.rest.BubbleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BubbleService {

    private final BubbleRestService bubbleRestService;

    @Autowired
    public BubbleService(BubbleRestService bubbleRestService) {
        this.bubbleRestService = bubbleRestService;
    }


    @Cacheable("bubbles")
    public List<Bubble> findBubblesByActiveTrueOrderByCreatedDesc() {
        return bubbleRestService.findBubblesByActiveTrueOrderByCreatedDesc();
    }

    @Cacheable("bubbles")
    public List<Bubble> findByUseruuid(String uuid) {
        return bubbleRestService.findByUseruuid(uuid);
    }

    @CacheEvict("bubbles")
    public void addBubbleMember(String bubbleuuid, String useruuid) {
        bubbleRestService.addBubbleMember(bubbleuuid, useruuid);
    }

    @CacheEvict("bubbles")
    public void removeFromBubble(String bubbleuuid, String useruuid) {
        bubbleRestService.removeBubbleMember(bubbleuuid, useruuid);
    }

    @CacheEvict("bubbles")
    public void saveBubble(Bubble bubble) {
        bubbleRestService.saveBubble(bubble);
    }

    @CacheEvict("bubbles")
    public void updateBubble(Bubble bubble) {
        bubbleRestService.updateBubble(bubble);
    }

    @CacheEvict("bubbles")
    public void removeAllMembers(String bubbleuuid) {
        bubbleRestService.removeAllMembers(bubbleuuid);
    }
}
