package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by hans on 23/06/2017.
 */

public interface UserStatusRepository {

    @Query(value = "SELECT " +
            "  yt.uuid, " +
            "  yt.useruuid, " +
            "  yt.status,\n" +
            "  yt.statusdate," +
            "  yt.allocation, yt.type " +
            "FROM userstatus yt INNER JOIN (" +
            "                          SELECT" +
            "                            uuid," +
            "                            useruuid," +
            "                            max(statusdate) created" +
            "                          FROM userstatus" +
            "                          GROUP BY useruuid" +
            "                        ) ss ON yt.statusdate = ss.created AND yt.useruuid = ss.useruuid WHERE status LIKE 'ACTIVE'", nativeQuery = true)
    List<UserStatus> findAllActive();

    @Query(value = "SELECT\n" +
            "  yt.uuid,\n" +
            "  yt.useruuid,\n" +
            "  yt.status,\n" +
            "  yt.statusdate,\n" +
            "  yt.allocation, yt.type \n" +
            "FROM userstatus yt INNER JOIN (\n" +
            "                          SELECT\n" +
            "                            uuid,\n" +
            "                            useruuid,\n" +
            "                            max(statusdate) created\n" +
            "                          FROM userstatus WHERE statusdate < :actualdate \n" +
            "                          GROUP BY useruuid\n" +
            "                        ) ss ON yt.statusdate = ss.created AND yt.useruuid = ss.useruuid WHERE status LIKE 'ACTIVE'", nativeQuery = true)
    List<UserStatus> findAllActiveByDate(@Param("actualdate") String actualdate);

    List<UserStatus> findAllByOrderByUserAscStatusdateAsc();

    List<UserStatus> findByUserAndTypeAndStatusOrderByStatusdateAsc(@Param("user") User user, @Param("type") ConsultantType type, @Param("status") StatusType status);

    void delete(Set<UserStatus> selectedItems);

    void save(UserStatus userStatus);
}
