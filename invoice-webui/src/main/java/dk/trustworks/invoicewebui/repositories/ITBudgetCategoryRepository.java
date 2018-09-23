package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.ITBudgetCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "itbudgetcategory", path = "itbudgetcategory")
public interface ITBudgetCategoryRepository extends CrudRepository<ITBudgetCategory, Integer> {

    List<ITBudgetCategory> findAll();

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(ITBudgetCategory entity);
}
