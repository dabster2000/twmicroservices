package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.SubContract;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;

/**
 * Created by hans on 23/06/2017.
 */
@Transactional
@RepositoryRestResource(collectionResourceRel = "subcontracts", path = "subcontracts")
public interface SubContractRepository extends ContractBaseRepository<SubContract> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(SubContract entity);
}
