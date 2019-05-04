package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@Transactional
@RepositoryRestResource(collectionResourceRel = "contracts", path = "contracts")
public interface ContractRepository extends CrudRepository<Contract, String> {

    @Cacheable("rate")
    @Query(value = "select cc.rate as price from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate and cc.useruuid like :useruuid AND t.uuid like :taskuuid and c.status in :statusList ", nativeQuery = true)
    Double findConsultantRateByWork(@Param("workDate") String workDate, @Param("useruuid") String useruuid, @Param("taskuuid") String taskuuid, @Param("statusList") String... statusList);

    @Cacheable("contract")
    @Query(value = "select c.* from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate and cc.useruuid like :useruuid AND t.uuid like :taskuuid AND c.status IN :statusList ", nativeQuery = true)
    Contract findContractByWork(@Param("workDate") String workDate, @Param("useruuid") String useruuid, @Param("taskuuid") String taskuuid, @Param("statusList") List<String> statusList);

    List<Contract> findByActiveFromBeforeAndActiveToAfterAndStatusIn(LocalDate activeTo, LocalDate activeFrom, ContractStatus... statusList);
    List<Contract> findByActiveFromLessThanEqualAndActiveToGreaterThanEqualAndStatusIn(LocalDate activeTo, LocalDate activeFrom, ContractStatus... statusList);

    List<Contract> findByClient(@Param("client") Client client);

    @Query(value = "select c.* from usermanager.contracts c " +
            "right join usermanager.contract_consultants cc on c.uuid = cc.contractuuid " +
            "where c.activefrom <= :activeon and c.activeto >= :activeon and cc.useruuid like :useruuid " +
            "and c.status in ('TIME','SIGNED','CLOSED')", nativeQuery = true)
    List<Contract> findTimeActiveConsultantContracts(@Param("useruuid") String useruuid, @Param("activeon") String activeon);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Contract entity);
}
