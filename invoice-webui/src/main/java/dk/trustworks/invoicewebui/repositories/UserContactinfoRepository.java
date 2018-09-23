package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserContactinfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "user_contactinfo", path = "user_contactinfo")
public interface UserContactinfoRepository extends CrudRepository<UserContactinfo, Integer> {

    List<UserContactinfo> findAll();
    Optional<UserContactinfo> findFirstByUser(@Param("user") User user);

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(UserContactinfo entity);
}
