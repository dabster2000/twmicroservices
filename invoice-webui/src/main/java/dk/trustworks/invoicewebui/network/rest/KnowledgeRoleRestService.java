package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.KnowledgeRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KnowledgeRoleRestService {

    @Value("#{environment.KNOWLEDGESERVICE_URL}")
    private String knowledgeServiceUrl;

    private final RestTemplate restTemplate;

    private final Map<String, KnowledgeRole> knowledgeRoleCache = new HashMap<>();

    @Autowired
    public KnowledgeRoleRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KnowledgeRole findByUsername(String useruuid) {
        if(knowledgeRoleCache.containsKey(useruuid)) return knowledgeRoleCache.get(useruuid);
        KnowledgeRole knowledgeRole = null;
        try {
            knowledgeRole = restTemplate.getForObject(knowledgeServiceUrl + "/knowledgeroles/" + useruuid, KnowledgeRole.class);
        } catch (HttpClientErrorException exception) {

        }
        //knowledgeRoleCache.put(knowledgeRole.getUseruuid(), knowledgeRole);
        return knowledgeRole;
    }

    public KnowledgeRole create(KnowledgeRole knowledgeRole) {
        knowledgeRoleCache.clear();
        String url = knowledgeServiceUrl +"/knowledgeroles";
        return restTemplate.postForObject(url, knowledgeRole, KnowledgeRole.class);
    }

    public void update(KnowledgeRole knowledgeRole) {
        knowledgeRoleCache.clear();
        String url = knowledgeServiceUrl +"/knowledgeroles/"+knowledgeRole.getUseruuid();
        restTemplate.put(url, knowledgeRole);
    }
}
