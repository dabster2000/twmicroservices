package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */

@Transactional
@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends CrudRepository<Role, String> {

}
