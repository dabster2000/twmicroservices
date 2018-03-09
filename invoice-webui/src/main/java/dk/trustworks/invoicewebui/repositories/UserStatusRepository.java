package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */

@RepositoryRestResource(collectionResourceRel = "statuses", path = "statuses")
public interface UserStatusRepository extends CrudRepository<UserStatus, String> {

    @Query(value = "SELECT\n" +
            "  yt.uuid,\n" +
            "  yt.useruuid,\n" +
            "  yt.status,\n" +
            "  yt.statusdate,\n" +
            "  yt.allocation\n" +
            "FROM userstatus yt INNER JOIN (\n" +
            "                          SELECT\n" +
            "                            uuid,\n" +
            "                            useruuid,\n" +
            "                            max(statusdate) created\n" +
            "                          FROM userstatus\n" +
            "                          GROUP BY useruuid\n" +
            "                        ) ss ON yt.statusdate = ss.created AND yt.useruuid = ss.useruuid WHERE status LIKE 'ACTIVE'", nativeQuery = true)
    List<UserStatus> findAllActive();

    @Query(value = "SELECT\n" +
            "  yt.uuid,\n" +
            "  yt.useruuid,\n" +
            "  yt.status,\n" +
            "  yt.statusdate,\n" +
            "  yt.allocation\n" +
            "FROM userstatus yt INNER JOIN (\n" +
            "                          SELECT\n" +
            "                            uuid,\n" +
            "                            useruuid,\n" +
            "                            max(statusdate) created\n" +
            "                          FROM userstatus WHERE statusdate < :actualdate \n" +
            "                          GROUP BY useruuid\n" +
            "                        ) ss ON yt.statusdate = ss.created AND yt.useruuid = ss.useruuid WHERE status LIKE 'ACTIVE'", nativeQuery = true)
    List<UserStatus> findAllActiveByDate(@Param("actualdate") String actualdate);


    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(UserStatus entity);
}
