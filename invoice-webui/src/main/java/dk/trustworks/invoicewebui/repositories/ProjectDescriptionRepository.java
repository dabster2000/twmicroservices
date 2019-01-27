package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.ProjectDescription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "projectdescriptions", path="projectdescriptions")
public interface ProjectDescriptionRepository extends CrudRepository<ProjectDescription, Integer> {

    List<ProjectDescription> findAll();

}
