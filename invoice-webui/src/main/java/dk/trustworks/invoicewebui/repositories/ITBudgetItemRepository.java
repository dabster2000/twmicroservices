package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.ItBudgetItem;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "itbudgetitem", path = "itbudgetitem")
public interface ITBudgetItemRepository extends CrudRepository<ItBudgetItem, Integer> {

    List<ItBudgetItem> findAll();
    List<ItBudgetItem> findByUser(@Param("user") User user);

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(ItBudgetItem entity);
}
