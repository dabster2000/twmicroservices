package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Photo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(collectionResourceRel = "photos", path = "photos")
public interface PhotoRepository extends CrudRepository<Photo, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Photo entity);

    Photo findByRelateduuid(String uuid);
}
