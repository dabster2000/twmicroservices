package dk.trustworks.repositories;

import dk.trustworks.model.Logo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "logos", path = "logos")
public interface LogoRepository extends CrudRepository<Logo, String> {

    Logo findByClientuuid(@Param("clientuuid") String clientuuid);
    List<Logo> findByClientuuidIn(@Param("clientuuid") List<String> clientuuids);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Logo entity);
}
