package dk.trustworks.repositories;

import java.util.Collection;
import java.util.List;
import dk.trustworks.model.User;
import dk.trustworks.model.projection.DetailsProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, String> {

    List<User> findAll();
    User findByUuid(@Param("uuid") String uuid);
    List<User> findByActiveTrue();
    List<User> findByActiveTrueOrderByUsername();
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(User entity);

    List<User> findByLastnameStartsWithIgnoreCase(String filterText);
}
