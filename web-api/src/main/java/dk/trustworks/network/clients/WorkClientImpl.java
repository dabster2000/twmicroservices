package dk.trustworks.network.clients;

import dk.trustworks.network.clients.feign.WorkClient;
import dk.trustworks.network.dto.Work;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

/**
 * Created by hans on 08/07/2017.
 */
@Component
public class WorkClientImpl implements WorkClient {
    @Override
    public Resources<Resource<Work>> findByYearAndMonth(int year, int month) {
        System.err.println("WorkClientImpl.loadDraftsByYearAndMonth");
        System.err.println("year = [" + year + "], month = [" + month + "]");
        return null;
    }
}
