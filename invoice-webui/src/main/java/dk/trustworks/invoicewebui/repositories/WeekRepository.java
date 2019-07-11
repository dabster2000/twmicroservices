package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Week;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "weeks", path = "weeks")
public interface WeekRepository extends CrudRepository<Week, String> {

    List<Week> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(@Param("weeknumber") int weeknumber, @Param("year") int year, @Param("user") String user);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Week entity);
}
