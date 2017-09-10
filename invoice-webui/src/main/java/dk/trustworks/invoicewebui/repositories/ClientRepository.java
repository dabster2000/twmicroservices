package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends CrudRepository<Client, String> {

    List<Client> findByActiveTrue();
    List<Client> findAllByOrderByNameAsc();

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Client entity);
}
