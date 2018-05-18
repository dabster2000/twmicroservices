package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Consultant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "consultants", path = "consultants")
public interface ConsultantRepository extends CrudRepository<Consultant, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Consultant entity);
}
