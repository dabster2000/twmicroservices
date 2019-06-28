package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Role;

/**
 * Created by hans on 23/06/2017.
 */

//@Transactional
//@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository {

    void delete(Role role);

    void save(Role role);
}
