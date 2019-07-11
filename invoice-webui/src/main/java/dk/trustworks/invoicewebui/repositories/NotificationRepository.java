package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */

@RepositoryRestResource(collectionResourceRel = "notifications", path = "notifications")
public interface NotificationRepository extends CrudRepository<Notification, String> {

    List<Notification> findByUseruuidAndAndExpirationdateAfter(String user, LocalDate expirationdate);

}
