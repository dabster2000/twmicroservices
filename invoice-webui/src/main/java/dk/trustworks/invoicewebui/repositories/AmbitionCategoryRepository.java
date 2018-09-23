package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.AmbitionCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "ambition_categories", path="ambition_categories")
public interface AmbitionCategoryRepository extends CrudRepository<AmbitionCategory, Integer> {


}
