package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "currency", path="currencies")
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

    List<Currency> findByCurrencytypeOrderByCollectedAsc(@Param("currencytype") String currencytype);

}
