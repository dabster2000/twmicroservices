package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.WorkWithRate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "work", path = "work")
public interface WorkRepository extends CrudRepository<Work, String> {

    Work findByDayAndMonthAndYearAndUserAndTask(int day, int month, int year, User user, Task task);

    @Query(value = "select w.*, cc.rate from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and  w.registered >= :fromdate AND w.registered < :todate " +
            "and w.workduration > 0 and c.status in :statusList ", nativeQuery = true)
    List<WorkWithRate> findWorkWithRateByPeriod(@Param("fromdate") LocalDate fromdate, @Param("todate") LocalDate todate, @Param("statusList") String... statusList);

    /**
     *
     * @param fromdate including date
     * @param todate including date
     * @param useruuid actual user
     * @return
     */
    @Query(value = "SELECT * FROM (SELECT *, STR_TO_DATE(CONCAT(w.year,'-',(w.month+1),'-',w.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work w) as k WHERE k.registered >= :fromdate AND k.registered <= :todate AND k.useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByPeriodAndUserUUID(@Param("fromdate") String fromdate,
                                       @Param("todate") String todate,
                                       @Param("useruuid") String useruuid);

    @Deprecated
    @Query(value = "SELECT * FROM (SELECT *, STR_TO_DATE(CONCAT(w.year,'-',(w.month+1),'-',w.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work w) as k WHERE k.registered >= :fromdate AND k.registered <= :todate", nativeQuery = true)
    List<Work> findByPeriod(@Param("fromdate") String fromdate, @Param("todate") String todate);

    @Query(value = "SELECT * FROM (SELECT *, STR_TO_DATE(CONCAT(w.year,'-',(w.month+1),'-',w.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work w) as k WHERE k.registered >= :fromdate AND k.registered <= :todate", nativeQuery = true)
    List<Work> findByPeriod(@Param("fromdate") LocalDate fromdate, @Param("todate") LocalDate todate);

    @Query(value = "select w.* from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and  w.registered >= :fromdate AND w.registered < :todate " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "and cc.rate > 0.0 ", nativeQuery = true)
    List<Work> findBillableWorkByPeriod(@Param("fromdate") String fromdate, @Param("todate") String todate);

    @Cacheable("billableWorkByUser")
    @Query(value = "select w.* from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= registered and c.activeto >= registered " +
            "and w.useruuid LIKE :useruuid " +
            "and w.workduration > 0 and c.status in ('TIME', 'SIGNED', 'CLOSED') " +
            "and cc.rate > 0.0 ", nativeQuery = true)
    List<Work> findBillableWorkByUser(@Param("useruuid") String useruuid);

    @Query(value = "select w.* from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= w.registered and c.activeto >= w.registered " +
            "and c.uuid like :contractuuid " +
            "and w.workduration > 0 " +
            "and cc.rate > 0.0 ", nativeQuery = true)
    List<Work> findWorkByContract(@Param("contractuuid") String contractuuid);

    @Query(value = "select sum(w.workduration * cc.rate) as used from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= w.registered and c.activeto >= w.registered " +
            "and c.uuid like :contractuuid " +
            "and w.workduration > 0 " +
            "and cc.rate > 0.0 ", nativeQuery = true)
    Double findAmountUsedByContract(@Param("contractuuid") String contractuuid);

    @Query(value = "select sum(w.workduration) as used from " +
            "(SELECT *, STR_TO_DATE(CONCAT(k.year,'-',(k.month+1),'-',k.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work k) as w " +
            "inner join task t on w.taskuuid = t.uuid " +
            "inner join project p on t.projectuuid = p.uuid " +
            "inner join user u on w.useruuid = u.uuid " +
            "inner join contract_project cp on p.uuid = cp.projectuuid " +
            "inner join contracts c on cp.contractuuid = c.uuid " +
            "inner join contract_consultants cc on c.uuid = cc.contractuuid and u.uuid = cc.useruuid " +
            "where c.activefrom <= w.registered and c.activeto >= w.registered " +
            "and  w.registered >= :fromdate AND w.registered < :todate " +
            "and c.uuid like :contractuuid " +
            "and w.workduration > 0 " +
            "and u.uuid LIKE :useruuid " +
            "and cc.rate > 0.0 ", nativeQuery = true)
    Double findHoursRegisteredOnContractByPeriod(@Param("contractuuid") String contractuuid, @Param("useruuid") String useruuid, @Param("fromdate") LocalDate fromdate, @Param("todate") LocalDate todate);

    @Query(value = "SELECT id, day, month, year, taskuuid, useruuid, w.workas as workas, sum(workduration) as workduration, '2017-05-17 08:09:35' created FROM work w WHERE w.year = :year AND w.month = :month GROUP BY taskuuid, useruuid", nativeQuery = true)
    List<Work> findByYearAndMonth(@Param("year") int year,
                                  @Param("month") int month);

    @Query(value = "select w.* from work w left join task t on w.taskuuid = t.uuid where w.year = :year and w.month = :month and t.projectuuid like :projectuuid", nativeQuery = true)
    List<Work> findByYearAndMonthAndProject(@Param("year") int year,
                                  @Param("month") int month,
                                  @Param("projectuuid") String projectuuid);

    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM work w WHERE w.taskuuid IN :taskuuid", nativeQuery = true)
    List<Work> findByTasks(@Param("taskuuid") List<String> taskuuid);

    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM work w WHERE w.taskuuid IN :taskuuid AND useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByUserAndTasks(@Param("useruuid") String useruuid, @Param("taskuuid") String... taskuuid);

    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM work w WHERE w.year = :year AND w.month = :month AND w.taskuuid IN :taskuuid AND useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByYearAndMonthAndUserAndTasks(@Param("year") int year, @Param("month") int month, @Param("useruuid") String useruuid, @Param("taskuuid") String... taskuuid);

    @Query(value = "select '2017-05-17 08:09:35' created, w.id, w.day as day, w.month as month, w.year as year, w.taskuuid as taskuuid, w.useruuid as useruuid, workduration as workduration, w.workas as workas from work w " +
            "left join task t on w.taskuuid = t.uuid " +
            "left join project p on t.projectuuid = p.uuid " +
            "where w.useruuid in :useruuids " +
            "and ((w.year*10000)+((w.month+1)*100)+w.day) between :fromdate and :todate " +
            "and t.projectuuid in :projectuuids " +
            "and w.workduration > 0.0", nativeQuery = true)
    List<Work> findByProjectsAndUsersAndDateRange(@Param("projectuuids") List<String> projectuuids, @Param("useruuids") List<String> useruuids, @Param("fromdate") String fromdate, @Param("todate") String todate);

    @Query(value = "SELECT w.* from work w " +
            "LEFT JOIN task t ON w.taskuuid = t.uuid " +
            "LEFT JOIN project p ON t.projectuuid = p.uuid " +
            "LEFT JOIN client c ON p.clientuuid = c.uuid " +
            "WHERE w.workduration > 0 AND t.type NOT LIKE 'SO' AND c.active = true " +
            "ORDER BY c.name;", nativeQuery = true)
    List<Work> findByActiveClients();


    @Override
    @CacheEvict("work")
    <S extends Work> Iterable<S> save(Iterable<S> entities);

    @Override
    @CacheEvict("work")
    <S extends Work> S save(S entity);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Work entity);
}
