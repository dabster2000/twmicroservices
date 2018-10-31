package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.ProjectDesctiptionProject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "projectdescription_projects", path="projectdescription_projects")
public interface ProjectDescriptionProjectRepository extends CrudRepository<ProjectDesctiptionProject, Integer> {



}
