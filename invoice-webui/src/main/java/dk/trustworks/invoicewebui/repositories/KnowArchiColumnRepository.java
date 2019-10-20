package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.KnowledgeArchitectureColumn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "architecture_column", path="architecture_column")
public interface KnowArchiColumnRepository extends CrudRepository<KnowledgeArchitectureColumn, Integer> {
}
