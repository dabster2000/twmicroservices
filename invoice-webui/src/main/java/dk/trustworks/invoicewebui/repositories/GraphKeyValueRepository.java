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
/*
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
*/
    @Cacheable("findRevenueByMonthByPeriod")
    @Query(value = "select w.id as uuid, DATE_FORMAT(w.registered, '%Y-%m-%d') as description, ROUND(SUM(w.workduration*cc.rate)) value from " +
            "work as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and w.registered >= :periodStart AND w.registered <= :periodEnd " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "GROUP BY MONTH(w.registered), YEAR(w.registered) " +
            "ORDER BY YEAR(w.registered), MONTH(w.registered) ", nativeQuery = true)
    List<GraphKeyValue> findRevenueByMonthByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findBillableHoursByMonthByPeriod")
    @Query(value = "select w.id as uuid, w.registered description, ROUND(SUM(w.workduration)) value from " +
            "work as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and w.registered >= :periodStart AND w.registered <= :periodEnd " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "and cc.rate > 0 " +
            "and u.uuid LIKE :useruuid " +
            "GROUP BY w.month, w.year " +
            "ORDER BY w.year, w.month ", nativeQuery = true)
    List<GraphKeyValue> findBillableHoursByMonthByPeriod(@Param("useruuid") String useruuid, @Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findConsultantRevenueByPeriod")
    @Query(value = "select u.uuid uuid, concat(u.firstname, ' ', u.lastname) description, ROUND(SUM(w.workduration*cc.rate)) value from " +
            "work as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and w.registered >= :periodStart AND w.registered <= :periodEnd " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "GROUP BY w.useruuid ORDER BY value DESC;", nativeQuery = true)
    List<GraphKeyValue> findConsultantRevenueByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Cacheable("findConsultantRevenueByPeriod")
    @Query(value = "select u.uuid uuid, concat(u.firstname, ' ', u.lastname) description, ROUND(SUM(w.workduration)) value from " +
            "work as w " +
            "left join task t on w.taskuuid = t.uuid " +
            "left join project p on t.projectuuid = p.uuid " +
            "left join user u on w.useruuid = u.uuid " +
            "left join contract_consultants cc on cc.useruuid = u.uuid " +
            "left join contract_project cp on p.uuid = cp.projectuuid " +
            "left join contracts c on cp.contractuuid = c.uuid and cc.contractuuid = c.uuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and w.registered >= :periodStart AND w.registered <= :periodEnd " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "GROUP BY w.useruuid ORDER BY value DESC;", nativeQuery = true)
    List<GraphKeyValue> findConsultantBillableHoursByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

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
            "and w.registered >= :periodStart AND w.registered <= :periodEnd " +
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
