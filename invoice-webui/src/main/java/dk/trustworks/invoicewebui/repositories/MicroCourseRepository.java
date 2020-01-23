package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.CkoCourse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "micro_courses", path="micro_courses")
public interface MicroCourseRepository extends CrudRepository<CkoCourse, Integer> {

    List<CkoCourse> findByActiveTrue();
    List<CkoCourse> findByTypeAndActiveTrueOrderByCreatedDesc(@Param("type") String type);

}
