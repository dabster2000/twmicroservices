package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.MainContract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@Transactional
@RepositoryRestResource(collectionResourceRel = "maincontracts", path = "maincontracts")
public interface MainContractRepository extends ContractBaseRepository<MainContract> {

    @Query(value = "select c.* from usermanager.contracts c " +
            "right join usermanager.contract_consultants cc on c.uuid = cc.contractuuid " +
            "where c.activefrom <= :activeon and c.activeto >= :activeon and cc.useruuid like :useruuid", nativeQuery = true)
    List<MainContract> findActiveConsultantContracts(@Param("useruuid") String useruuid, @Param("activeon") String activeon);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(MainContract entity);
}
