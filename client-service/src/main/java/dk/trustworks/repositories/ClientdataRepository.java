package dk.trustworks.repositories;

import dk.trustworks.model.Clientdata;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "clientdata", path = "clientdata")
public interface ClientdataRepository extends PagingAndSortingRepository<Clientdata, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Clientdata entity);
}
