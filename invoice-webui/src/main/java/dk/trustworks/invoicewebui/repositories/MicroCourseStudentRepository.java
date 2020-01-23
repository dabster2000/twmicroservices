package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.CkoCourse;
import dk.trustworks.invoicewebui.model.CkoCourseStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "students", path="students")
public interface MicroCourseStudentRepository extends CrudRepository<CkoCourseStudent, Integer> {
    List<CkoCourseStudent> findByUseruuid(@Param("student") String user);
    CkoCourseStudent findByCkoCourseAndUseruuid(@Param("ckocourse") CkoCourse ckoCourse, @Param("student") String user);
}
