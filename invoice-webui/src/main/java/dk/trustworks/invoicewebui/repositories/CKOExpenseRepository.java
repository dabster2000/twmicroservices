package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "cko_expenses", path="cko_expenses")
public interface CKOExpenseRepository extends CrudRepository<CKOExpense, Integer> {


    List<CKOExpense> findCKOExpenseByUser(@Param("user") User user);

}
