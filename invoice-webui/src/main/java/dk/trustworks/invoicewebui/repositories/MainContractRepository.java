package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.MainContract;
import org.springframework.data.jpa.repository.Query;
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
@RepositoryRestResource(collectionResourceRel = "maincontracts", path = "maincontracts")
public interface MainContractRepository extends ContractBaseRepository<MainContract> {
/*
    @Query(value = "select cc.rate as price from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate ", nativeQuery = true)*/
    List<MainContract> findByActiveFromBeforeAndActiveToAfter(LocalDate activeTo, LocalDate activeFrom);

    @Query(value = "select c.* from usermanager.contracts c " +
            "right join usermanager.contract_consultants cc on c.uuid = cc.contractuuid " +
            "where c.activefrom <= :activeon and c.activeto >= :activeon and cc.useruuid like :useruuid " +
            "and c.status in ('TIME','SIGNED')", nativeQuery = true)
    List<MainContract> findTimeActiveConsultantContracts(@Param("useruuid") String useruuid, @Param("activeon") String activeon);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(MainContract entity);
}
