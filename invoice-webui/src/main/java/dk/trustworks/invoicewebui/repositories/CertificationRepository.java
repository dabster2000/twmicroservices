package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Certification;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "certifications", path="certifications")
public interface CertificationRepository extends CrudRepository<Certification, Integer> {

    List<Certification> findByUser(@Param("user") User user);

}
