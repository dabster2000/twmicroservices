package dk.trustworks.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "tasks", path="tasks")
public interface TaskRepository extends CrudRepository<Task, String> {

    Task findByUuid(@Param("uuid") String uuid);
    List<Task> findByProjectuuid(@Param("projectuuid") String projectuuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Task entity);
}
