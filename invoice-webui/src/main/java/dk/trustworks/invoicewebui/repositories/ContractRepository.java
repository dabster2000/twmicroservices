package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 23/06/2017.
 */
/*
@Transactional
@RepositoryRestResource(collectionResourceRel = "contracts", path = "contracts")
public interface ContractRepository extends CrudRepository<Contract, String> {

    //@Cacheable("rate")
    /*
    @Query(value = "select cc.rate as price from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate and cc.useruuid like :useruuid AND t.uuid like :taskuuid and c.status in :statusList ", nativeQuery = true)
    Double findConsultantRateByWork(@Param("workDate") String workDate, @Param("useruuid") String useruuid, @Param("taskuuid") String taskuuid, @Param("statusList") String... statusList);

     */

    //@Cacheable("contract")
    /*
    @Query(value = "select c.* from usermanager.contracts c" +
            "    right join contract_project pc ON  pc.contractuuid = c.uuid" +
            "    right join project p ON p.uuid = pc.projectuuid" +
            "    right join task t ON t.projectuuid = p.uuid" +
            "    right join contract_consultants cc ON c.uuid = cc.contractuuid" +
            "    where c.activefrom <= :workDate and c.activeto >= :workDate and cc.useruuid like :useruuid AND t.uuid like :taskuuid AND c.status IN :statusList ", nativeQuery = true)
    Contract findContractByWork(@Param("workDate") String workDate, @Param("useruuid") String useruuid, @Param("taskuuid") String taskuuid, @Param("statusList") List<String> statusList);



    List<Contract> findByActiveFromBeforeAndActiveToAfterAndStatusIn(LocalDate activeTo, LocalDate activeFrom, ContractStatus... statusList);
    List<Contract> findByActiveFromLessThanEqualAndActiveToGreaterThanEqualAndStatusIn(LocalDate activeTo, LocalDate activeFrom, ContractStatus... statusList);

    List<Contract> findByClientuuid(@Param("clientuuid") String clientuuid);

    @Query(value = "select c.* from usermanager.contracts c " +
            "right join usermanager.contract_consultants cc on c.uuid = cc.contractuuid " +
            "where c.activefrom <= :activeon and c.activeto >= :activeon and cc.useruuid like :useruuid " +
            "and c.status in ('TIME','SIGNED','CLOSED')", nativeQuery = true)
    List<Contract> findTimeActiveConsultantContracts(@Param("useruuid") String useruuid, @Param("activeon") String activeon);

    List<Contract> findAll();

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Contract entity);

     */
/*
    @Query(value = "select c.uuid, " +
            "       c.contracttype, " +
            "       c.clientuuid, " +
            "       c.created, " +
            "       c.activefrom, " +
            "       c.activeto, " +
            "       c.amount, " +
            "       c.parentuuid, " +
            "       c.status, " +
            "       c.note, " +
            "       c.clientdatauuid, " +
            "       c.name, " +
            "       c.refid " +
            "from usermanager.contracts c " +
            "    left join usermanager.contract_project cp on cp.contractuuid = c.uuid " +
            "    right join usermanager.project p on p.uuid = cp.projectuuid " +
            "where p.uuid like :projectuuid ", nativeQuery = true)
    List<Contract> findByProjectuuid(@Param("projectuuid") String projectuuid);

 */
//}
