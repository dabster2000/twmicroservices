package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.ExpenseDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "expensedetails", path="expensedetails")
public interface ExpenseDetailsRepository extends CrudRepository<ExpenseDetails, String> {

    List<ExpenseDetails> findByExpensedateAndAccountnumber(@Param("expensedate") LocalDate expensedate, @Param("accountnumber") int accountnumber);

}
