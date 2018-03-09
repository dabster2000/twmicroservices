package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.network.clients.VimeoAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class KnowledgePreloader {

    private final VimeoAPI vimeoAPI;

    @PostConstruct
    public void onStartup() {
        loadTrustworksKnowledge();
    }

    @Autowired
    public KnowledgePreloader(VimeoAPI vimeoAPI) {
        this.vimeoAPI = vimeoAPI;
    }

    private String[] trustworksKnowledge;

    @Scheduled(cron = "0 1 1 * * ?")
    public void loadTrustworksKnowledge() {
        trustworksKnowledge = vimeoAPI.getTrustworksKnowledge();
    }

    public String[] getTrustworksKnowledge() {
        return trustworksKnowledge;
    }

}
