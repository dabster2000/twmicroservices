package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Contract;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;

/**
 * Created by hans on 23/06/2017.
 */
@Transactional
@RepositoryRestResource(collectionResourceRel = "maincontracts", path = "maincontracts")
public interface MainContractRepository extends ContractBaseRepository<Contract> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Contract entity);
}