package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.ReminderHistory;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "reminder_histories", path="reminder_histories")
public interface ReminderHistoryRepository extends CrudRepository<ReminderHistory, Integer> {

    ReminderHistory findFirstByUserAndTargetuuidAndType(@Param("user") User user, @Param("targetuuid") String targetuuid, @Param("type") ReminderType type);
    ReminderHistory findFirstByTypeAndUserOrderByTransmissionDateDesc(@Param("type") ReminderType type, @Param("user") User user);
    List<ReminderHistory> findByTypeAndUserOrderByTransmissionDateDesc(@Param("type") ReminderType type, @Param("user") User user);
    List<ReminderHistory> findByTargetuuidAndType(@Param("targetuuid") String targetuuid, @Param("type") ReminderType type);

}
