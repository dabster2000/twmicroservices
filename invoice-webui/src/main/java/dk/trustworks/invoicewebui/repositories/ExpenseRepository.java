package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "expenses", path="expenses")
public interface ExpenseRepository extends CrudRepository<Expense, String> {

    List<Expense> findByPeriod(@Param("period") LocalDate period);

    @Transactional
    void deleteByPeriod(@Param("period") LocalDate period);

}
