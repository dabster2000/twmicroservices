package dk.trustworks.repositories;

import dk.trustworks.model.Week;
import dk.trustworks.model.Work;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "weeks", path = "weeks")
public interface WeekRepository extends CrudRepository<Week, String> {

    List<Week> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(@Param("weeknumber") int weeknumber, @Param("year") int year, @Param("useruuid") String useruuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Week entity);
}
