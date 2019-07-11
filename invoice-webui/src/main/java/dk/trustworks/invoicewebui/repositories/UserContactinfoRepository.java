package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserContactinfo;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 23/06/2017.
 */
//@RepositoryRestResource(collectionResourceRel = "user_contactinfo", path = "user_contactinfo")
public interface UserContactinfoRepository {

    List<UserContactinfo> findAll();
    Optional<UserContactinfo> findFirstByUser(@Param("user") User user);

    public void save(UserContactinfo contactinfo);
}
