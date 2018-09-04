package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "document", path = "document")
public interface DocumentRepository extends CrudRepository<Document, Integer> {

    List<Document> findAll();

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(Document entity);

}
