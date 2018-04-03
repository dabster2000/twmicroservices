package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Bubble;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubbles", path="bubbles")
public interface BubbleRepository extends CrudRepository<Bubble, String> {

    List<Bubble> findBubblesByActiveTrue();

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Bubble entity);


}
