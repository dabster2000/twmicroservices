package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(collectionResourceRel = "files", path = "files")
public interface FileRepository extends CrudRepository<File, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(File entity);

    File findByRelateduuid(String uuid);
}
