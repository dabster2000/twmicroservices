package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Ambition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "ambitions", path="ambitions")
public interface AmbitionRepository extends CrudRepository<Ambition, Integer> {

    List<Ambition> findAmbitionByActiveIsTrue();
    List<Ambition> findAmbitionByOfferingIsTrueAndActiveIsTrue();
    List<Ambition> findAmbitionByActiveIsTrueAndCategory(@Param("category") String category);

}
