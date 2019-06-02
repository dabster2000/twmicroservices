package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "projects",path="projects")
public interface ProjectRepository extends CrudRepository<Project, String> {

    List<Project> findByLockedTrue();
    List<Project> findAllByOrderByNameAsc();
    List<Project> findAllByActiveTrueOrderByNameAsc();
    List<Project> findByClientAndActiveTrueOrderByNameAsc(Client client);
    List<Project> findByClientOrderByNameAsc(Client client);
    List<Project> findByClientdata(Clientdata clientdata);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Project entity);
}
