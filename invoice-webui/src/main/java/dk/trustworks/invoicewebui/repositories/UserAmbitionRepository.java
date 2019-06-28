package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.UserAmbition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "user_ambitions", path="user_ambitions")
public interface UserAmbitionRepository extends CrudRepository<UserAmbition, Integer> {

    List<UserAmbition> findByUseruuid(@Param("user") String user);

}
