package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;

/**
 * Created by hans on 23/06/2017.
 */
@Transactional
@RepositoryRestResource(collectionResourceRel = "contracts", path = "contracts")
public interface ContractRepository extends ContractBaseRepository<Contract> {

    @Query(value = "select cc.rate as price from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate and cc.useruuid like :useruuid AND t.uuid like :taskuuid ", nativeQuery = true)
    Double findConsultantRateByWork(@Param("workDate") String workDate, @Param("useruuid") String useruuid, @Param("taskuuid") String taskuuid);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Contract entity);
}
