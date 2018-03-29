package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, String> {

    List<User> findAll();
    User findByUuid(@Param("uuid") String uuid);
    List<User> findByActiveTrue();
    List<User> findByOrderByUsername();
    List<User> findByActiveTrueOrderByUsername();
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    User findByUsername(@Param("username") String username);
    User findBySlackusername(@Param("slackusername") String slackusername);

    @Query(value = "SELECT allocation FROM user u RIGHT JOIN ( " +
            "select t.useruuid, t.status, t.statusdate, t.allocation " +
            "from userstatus t " +
            "inner join ( " +
            "select useruuid, status, max(statusdate) as MaxDate " +
            "from userstatus  WHERE statusdate < :statusdate " +
            "group by useruuid " +
            ") " +
            "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
            ") usi ON u.uuid = usi.useruuid WHERE u.uuid LIKE :useruuid ;", nativeQuery = true)
    int calculateCapacityByMonthByUser(@Param("useruuid") String useruuid, @Param("statusdate") String statusdate);


    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(User entity);

    List<User> findByLastnameStartsWithIgnoreCase(String filterText);
}
