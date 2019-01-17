package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.LogEvent;
import dk.trustworks.invoicewebui.model.enums.LogType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "logevent", path="logevent")
public interface LogEventRepository extends CrudRepository<LogEvent, Integer> {

    List<LogEvent> findByType(@Param("type") LogType type);

}
