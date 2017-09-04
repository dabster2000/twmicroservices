package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Budget;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "budgets", path="budgets")
public interface BudgetRepository extends CrudRepository<Budget, String> {

    @Override
    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w ORDER BY w.taskuuid, w.useruuid, w.year, w.month;", nativeQuery = true)
    Iterable<Budget> findAll();

    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w WHERE taskuuid LIKE :taskuuid ORDER BY w.taskuuid, w.useruuid, w.year, w.month;", nativeQuery = true)
    List<Budget> findByTaskuuid(@Param("taskuuid") String taskUUID);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Budget entity);
}
