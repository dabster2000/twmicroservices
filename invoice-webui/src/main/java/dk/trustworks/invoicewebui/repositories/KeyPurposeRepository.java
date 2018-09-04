package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.KeyPurpose;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "keypurpose", path = "keypurpose")
public interface KeyPurposeRepository extends CrudRepository<KeyPurpose, String> {

    List<KeyPurpose> findAll();
    List<KeyPurpose> findByUserOrderByNumAsc(@Param("user") User user);
    KeyPurpose findByUserAndNum(@Param("user") User user, @Param("num") int num);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(KeyPurpose entity);

}
