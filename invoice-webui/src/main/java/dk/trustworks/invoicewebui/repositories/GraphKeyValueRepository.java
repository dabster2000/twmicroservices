package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "graphkeyvalues", path="graphkeyvalues")
public interface GraphKeyValueRepository extends CrudRepository<GraphKeyValue, String> {

    @Cacheable("findProjectRevenueByPeriod")
    @Query(value = "SELECT p.name description, p.uuid uuid, ROUND(SUM(w.workduration * twc.price)) value " +
            "                FROM work w " +
            "                INNER JOIN user u ON w.useruuid = u.uuid " +
            "                INNER JOIN taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
            "                INNER JOIN task t ON t.uuid = twc.taskuuid " +
            "                INNER JOIN project p ON p.uuid = t.projectuuid " +
            "                WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
            "                GROUP BY p.uuid ORDER BY value DESC;", nativeQuery = true)
    List<GraphKeyValue> findProjectRevenueByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findRevenueByMonthByPeriod")
    @Query(value = "SELECT w.id uuid, CONCAT(w.year,'-',w.month+1,'-','01') description, ROUND(SUM(w.workduration * twc.price)) value " +
            "                            FROM work w " +
            "                            INNER JOIN taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = w.useruuid " +
            "                            WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd AND w.workduration > 0 " +
            "                            GROUP BY w.month, w.year;", nativeQuery = true)
    List<GraphKeyValue> findRevenueByMonthByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findBudgetByMonthByPeriod")
    @Query(value = "SELECT w.taskuuid uuid, CONCAT(w.year,'-',w.month+1,'-','01') description, ROUND(SUM(w.budget)) value " +
            "    FROM taskworkerconstraint_latest w " +
            "    WHERE ((w.year*10000)+((w.month+1)*100)+1) between :periodStart and :periodEnd AND w.budget > 0 " +
            "    GROUP BY w.month;", nativeQuery = true)
    List<GraphKeyValue> findBudgetByMonthByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findBudgetByMonthAndHistory")
    @Query(value = "select yt.taskuuid uuid, CONCAT(yt.year,'-',yt.month+1,'-','01') description, ROUND(SUM(yt.budget)) value " +
            "FROM taskworkerconstraintbudget yt " +
            "join ( " +
            "select twcb.uuid AS uuid, " +
            "twcb.month AS month, " +
            "twcb.year AS year, " +
            "max(twcb.created) AS created, " +
            "twcb.taskuuid AS taskuuid, " +
            "twcb.useruuid AS useruuid " +
            "from taskworkerconstraintbudget twcb WHERE twcb.created < :created AND twcb.month = :month AND twcb.year = :year " +
            "group by twcb.month, twcb.year, twcb.useruuid, twcb.taskuuid " +
            ") ss " +
            "on (yt.month = ss.month) and (yt.year = ss.year) and (yt.created = ss.created) and (yt.useruuid = ss.useruuid) and (yt.taskuuid = ss.taskuuid);", nativeQuery = true)
    List<GraphKeyValue> findBudgetByMonthAndHistory(@Param("month") int month, @Param("year") int year, @Param("created") String created);

    @Cacheable("findConsultantRevenueByPeriod")
    @Query(value = "SELECT concat(u.firstname, ' ', u.lastname) description, u.uuid uuid, SUM(w.workduration * twc.price) value  " +
            "FROM work w " +
            "INNER JOIN user u ON w.useruuid = u.uuid " +
            "INNER JOIN taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
            "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
            "GROUP BY w.useruuid ORDER BY value DESC;", nativeQuery = true)
    List<GraphKeyValue> findConsultantRevenueByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("countConsultantsPerProject")
    @Query(value = "SELECT p.uuid uuid, p.name description, COUNT(DISTINCT w.useruuid) value FROM usermanager.work w " +
            "LEFT JOIN usermanager.task t ON t.uuid = w.taskuuid " +
            "LEFT JOIN usermanager.project p ON p.uuid = t.projectuuid " +
            "LEFT JOIN " +
            "(SELECT w.useruuid useruuid, p.uuid projectuuid, p.name name, SUM(w.workduration) duration FROM usermanager.work w " +
            "LEFT JOIN usermanager.task t ON t.uuid = w.taskuuid " +
            "LEFT JOIN usermanager.project p ON p.uuid = t.projectuuid " +
            "GROUP BY w.useruuid, p.uuid " +
            "HAVING SUM(w.workduration) > 0) u ON u.useruuid = w.useruuid AND u.projectuuid = p.uuid " +
            "WHERE w.workduration > 0 AND u.duration > 30 AND p.uuid NOT LIKE 'fdfbb1a1-bbae-48a1-955d-e681153d6731' " +
            "AND ((w.year*10000)+((w.month+1)*100)+w.day) BETWEEN :periodStart AND :periodEnd " +
            "GROUP BY p.uuid ORDER BY value DESC;", nativeQuery = true)
    List<GraphKeyValue> countConsultantsPerProject(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);


    /*
    Antal projekter per konsulent
    SELECT u.description description, SUM(u.value) value FROM
(SELECT w.useruuid useruuid, CONCAT(us.firstname, ' ', us.lastname) description, p.name name, COUNT(DISTINCT p.uuid) value FROM usermanager.work_latest w
LEFT JOIN usermanager.task t ON t.uuid = w.taskuuid
LEFT JOIN usermanager.project p ON p.uuid = t.projectuuid
LEFT JOIN usermanager.user us ON us.uuid = w.useruuid
WHERE w.workduration > 0 AND p.uuid NOT LIKE 'fdfbb1a1-bbae-48a1-955d-e681153d6731'
AND ((w.year*10000)+((w.month+1)*100)+w.day) between 20160701 and 20170630
GROUP BY w.useruuid, p.uuid
HAVING SUM(w.workduration) > 20) u GROUP BY u.useruuid ORDER BY value DESC;
     */

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(GraphKeyValue entity);
}
