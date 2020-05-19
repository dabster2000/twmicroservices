package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.ContractProject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "contractprojects", path = "contractprojects")
public interface ContractProjectRepository extends CrudRepository<ContractProject, String> {

}
