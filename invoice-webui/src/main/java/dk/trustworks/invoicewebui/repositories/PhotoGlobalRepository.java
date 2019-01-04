package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.PhotoGlobal;
import dk.trustworks.invoicewebui.model.enums.PhotoGlobalType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(collectionResourceRel = "photos_global", path = "photos_global")
public interface PhotoGlobalRepository extends CrudRepository<PhotoGlobal, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(PhotoGlobal entity);

    PhotoGlobal findByType(PhotoGlobalType type);
}
