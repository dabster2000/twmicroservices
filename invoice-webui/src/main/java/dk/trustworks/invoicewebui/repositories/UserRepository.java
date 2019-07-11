package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
public interface UserRepository {

    List<User> findByOrderByUsername();
    User findByUsername(@Param("username") String username);
    User findBySlackusername(@Param("slackusername") String slackusername);

    @Query(value = "SELECT u.uuid, u.created, u.active, u.email, u.firstname, u.lastname, u.password, u.username, u.slackusername, u.birthday FROM user u " +
            "LEFT JOIN " +
            "(SELECT yt.uuid, yt.useruuid, yt.status, yt.statusdate, yt.allocation, yt.type FROM userstatus yt " +
            "INNER JOIN " +
            "(SELECT uuid, useruuid, max(statusdate) created FROM userstatus WHERE statusdate <= :date GROUP BY useruuid) " +
            "ss ON yt.statusdate = ss.created AND yt.useruuid = ss.useruuid) kk on u.uuid = kk.useruuid WHERE kk.status IN :consultantStatusList AND kk.type IN :consultantTypes ORDER BY u.username;", nativeQuery = true)
    List<User> findUsersByDateAndStatusListAndTypes(@Param("date") String date, @Param("consultantStatusList") String[] consultantStatusList, @Param("consultantTypes") String... consultantTypes);

    @Query(value = "SELECT COALESCE(sum(allocation), 0) FROM user u RIGHT JOIN ( " +
            "select t.useruuid, t.status, t.statusdate, t.allocation " +
            "from userstatus t " +
            "inner join ( " +
            "select useruuid, status, max(statusdate) as MaxDate " +
            "from userstatus  WHERE statusdate <= :statusdate " +
            "group by useruuid " +
            ") " +
            "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
            ") usi ON u.uuid = usi.useruuid WHERE u.uuid LIKE :useruuid ;", nativeQuery = true)
    int calculateCapacityByMonthByUser(@Param("useruuid") String useruuid, @Param("statusdate") String statusdate);


    public void save(User user);

    public User findOne(String userUUID);
}
