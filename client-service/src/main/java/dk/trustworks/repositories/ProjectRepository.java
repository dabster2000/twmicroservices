package dk.trustworks.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "projects",path="projects")
public interface ProjectRepository extends CrudRepository<Project, String> {

    List<Project> findProjectsByUuidIn(@Param("uuid") String[] uuids);

    List<Project> findByClientdatauuid(@Param("clientdatauuid") String clientdatauuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Project entity);
}
