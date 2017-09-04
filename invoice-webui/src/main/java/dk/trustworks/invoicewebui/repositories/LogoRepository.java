package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Logo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(collectionResourceRel = "logos", path = "logos")
public interface LogoRepository extends CrudRepository<Logo, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Logo entity);
}
