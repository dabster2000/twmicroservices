package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Reminder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "reminders", path="reminders")
public interface ReminderRepository extends CrudRepository<Reminder, Integer> {


}
