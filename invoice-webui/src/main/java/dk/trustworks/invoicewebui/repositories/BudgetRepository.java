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

    @Query(value = "SELECT SUM(w.budget / (( " +
            "SELECT twc.price FROM taskworkerconstraint twc WHERE twc.taskuuid LIKE :taskuuid AND twc.useruuid LIKE :useruuid " +
            "))) - ( " +
            "SELECT SUM(w.workduration) FROM work w WHERE w.taskuuid LIKE :taskuuid AND w.useruuid LIKE :useruuid " +
            ") FROM taskworkerconstraint_latest w WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid ORDER BY w.taskuuid, w.useruuid, w.year, w.month;", nativeQuery = true)
    Double findBudgetLeftByTaskuuidAndUseruuid(@Param("taskuuid") String taskuuid, @Param("useruuid") String useruuid);

    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w WHERE ((w.year*10000)+((w.month+1)*100))+1 between :periodStart and :periodEnd and budget > 0", nativeQuery = true)
    List<Budget> findByPeriod(@Param("periodStart") int periodStart,
                            @Param("periodEnd") int periodEnd);

    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w WHERE month = :month AND year = :year", nativeQuery = true)
    List<Budget> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w WHERE month = :month AND year = :year AND useruuid LIKE :useruuid", nativeQuery = true)
    List<Budget> findByMonthAndYearAndUseruuid(@Param("month") int month, @Param("year") int year, @Param("useruuid") String useruuid);

    @Query(value = "SELECT UUID() uuid, w.month, w.year, w.taskuuid, w.useruuid, w.budget, w.created FROM taskworkerconstraint_latest w WHERE ((w.year*10000)+((w.month+1)*100))+1 between :periodStart and :periodEnd and w.useruuid = :useruuid and budget > 0", nativeQuery = true)
    List<Budget> findByPeriodAndUseruuid(@Param("periodStart") int periodStart,
                                         @Param("periodEnd") int periodEnd,
                                         @Param("useruuid") String useruuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Budget entity);


}
