package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "clientdata", path = "clientdata")
public interface ClientdataRepository extends CrudRepository<Clientdata, String> {

    List<Clientdata> findByClient(@Param("client") Client client);

    //@Override @RestResource(exported = false) void delete(String id);
    //@Override @RestResource(exported = false) void delete(Clientdata entity);
}
