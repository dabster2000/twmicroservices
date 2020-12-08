package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.CKOExpense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;
/*
@Transactional
@RepositoryRestResource(collectionResourceRel = "cko_expenses", path="cko_expenses")
public interface CKOExpenseRepository extends CrudRepository<CKOExpense, Integer> {

    List<CKOExpense> findAll();
    List<CKOExpense> findCKOExpenseByUseruuid(@Param("user") String useruuid);
    List<CKOExpense> findByDescription(String description);
}


 */