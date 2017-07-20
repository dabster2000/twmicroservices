package dk.trustworks.repositories;

import dk.trustworks.model.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "work", path = "work")
public interface WorkRepository extends PagingAndSortingRepository<Work, String> {


    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM timemanager.work_latest w WHERE w.year = :year AND w.month = :month AND w.day = :day AND w.taskuuid LIKE :taskuuid AND w.useruuid LIKE :useruuid", nativeQuery = true)
    List<Work> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(@Param("year") int year,
                                                              @Param("month") int month,
                                                              @Param("day") int day,
                                                              @Param("taskuuid") String taskuuid,
                                                              @Param("useruuid") String useruuid);

    @Query(value = "SELECT *, '2017-05-17 08:09:35' created FROM timemanager.work_latest w WHERE w.year = :year AND w.month = :month", nativeQuery = true)
    List<Work> findByYearAndMonth(@Param("year") int year,
                                  @Param("month") int month);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Work entity);
}
