package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.IncomeForecast;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "incomeforecast", path="incomeforecast")
public interface IncomeForcastRepository extends CrudRepository<IncomeForecast, String> {

    List<IncomeForecast> findByCreatedAndItemtypeOrderBySortAsc(@Param("created") Date created, @Param("itemtype") String itemtype);

    @Transactional
    void deleteByCreatedAndItemtype(@Param("created") Date created, @Param("itemtype") String itemtype);

}
