package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.CKOCertification;
import dk.trustworks.invoicewebui.model.CKOExpense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RepositoryRestResource(collectionResourceRel = "cko_certifications", path="cko_certifications")
public interface CKOCertificationsRepository extends CrudRepository<CKOCertification, Integer> {
    List<CKOCertification> findAll();
}
