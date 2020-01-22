package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.MicroCourse;
import dk.trustworks.invoicewebui.model.MicroCourseStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "students", path="students")
public interface MicroCourseStudentRepository extends CrudRepository<MicroCourseStudent, Integer> {

    List<MicroCourseStudent> findByMicroCourse(@Param("microcourse") MicroCourse microCourse);
    List<MicroCourseStudent> findByUseruuid(@Param("student") String user);
    MicroCourseStudent findByMicroCourseAndUseruuid(@Param("microcourse") MicroCourse microCourse, @Param("student") String user);

}
