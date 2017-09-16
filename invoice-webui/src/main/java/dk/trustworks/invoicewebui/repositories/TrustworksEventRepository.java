package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.EventType;
import dk.trustworks.invoicewebui.model.TrustworksEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface TrustworksEventRepository extends CrudRepository<TrustworksEvent, String> {

    List<TrustworksEvent> findByEventdateBetweenOrderByEventdateAsc(Date from, Date to);
    List<TrustworksEvent> findByEventdateBetweenOrEventtype(Date from, Date to, EventType eventType);

}
