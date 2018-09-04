package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "consultants", path = "consultants")
public interface ConsultantRepository extends CrudRepository<Consultant, String> {

    List<Consultant> findByStatus(@Param("status") StatusType status);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Consultant entity);

    List<Consultant> findByTypeAndStatus(@Param("consultant") ConsultantType consultant, @Param("status") StatusType status);
}
