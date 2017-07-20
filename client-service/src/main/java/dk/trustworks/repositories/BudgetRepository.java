package dk.trustworks.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.model.Budget;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "budgets", path="budgets")
public interface BudgetRepository extends PagingAndSortingRepository<Budget, String> {
    //@Override @RestResource(exported = false) void update(String id);
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Budget entity);
}
