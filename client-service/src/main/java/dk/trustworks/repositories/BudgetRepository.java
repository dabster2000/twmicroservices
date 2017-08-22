package dk.trustworks.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.model.Budget;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "budgets", path="budgets")
public interface BudgetRepository extends CrudRepository<Budget, String> {

    @Override
    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM clientmanager.taskworkerconstraint_latest w ORDER BY w.taskuuid, w.useruuid, w.year, w.month;", nativeQuery = true)
    Iterable<Budget> findAll();

    //@Override @RestResource(exported = false) void update(String id);
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Budget entity);
}
