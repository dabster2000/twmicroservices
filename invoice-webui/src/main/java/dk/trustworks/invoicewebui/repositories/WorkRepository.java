package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "work", path = "work")
public interface WorkRepository extends CrudRepository<Work, String> {


    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM work_latest w WHERE w.year = :year AND w.month = :month AND w.day = :day AND w.taskuuid LIKE :taskuuid AND w.useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(@Param("year") int year,
                                                              @Param("month") int month,
                                                              @Param("day") int day,
                                                              @Param("taskuuid") String taskuuid,
                                                              @Param("useruuid") String useruuid);
    //uuid, day, month, year, taskuuid, useruuid, workduration, '2017-05-17 08:09:35' created
    @Query(value = "SELECT * FROM (SELECT *, STR_TO_DATE(CONCAT(w.year,'-',(w.month+1),'-',w.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work_latest w) as k WHERE k.registered >= :fromdate AND k.registered <= :todate AND k.useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByPeriodAndUserUUID(@Param("fromdate") String fromdate,
                                       @Param("todate") String todate,
                                       @Param("useruuid") String useruuid);

    @Cacheable("work")
    @Query(value = "SELECT * FROM (SELECT *, STR_TO_DATE(CONCAT(w.year,'-',(w.month+1),'-',w.day), '%Y-%m-%d') as registered, '2017-05-17 08:09:35' created FROM work_latest w) as k WHERE k.registered >= :fromdate AND k.registered <= :todate", nativeQuery = true)
    List<Work> findByPeriod(@Param("fromdate") String fromdate,
                                       @Param("todate") String todate);

    //@Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM timemanager.work_latest w WHERE w.year = :year AND w.month = :month", nativeQuery = true)
    @Query(value = "SELECT uuid, day, month, year, taskuuid, useruuid, sum(workduration) as workduration, '2017-05-17 08:09:35' created FROM work_latest w WHERE w.year = :year AND w.month = :month GROUP BY taskuuid, useruuid", nativeQuery = true)
    List<Work> findByYearAndMonth(@Param("year") int year,
                                  @Param("month") int month);

    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM work_latest w WHERE w.taskuuid LIKE :taskuuid", nativeQuery = true)
    List<Work> findByTask(@Param("taskuuid") String taskuuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Work entity);
}
