package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Document;
import dk.trustworks.invoicewebui.model.enums.DocumentType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by hans on 23/06/2017.
 */
@RepositoryRestResource(collectionResourceRel = "documents", path = "documents")
public interface DocumentRepository extends CrudRepository<Document, Integer> {

    List<Document> findAll();
    List<Document> findByType(@Param("contract") DocumentType contract);
    List<Document> findByUseruuidAndType(@Param("user") String useruuid, @Param("contract") DocumentType contract);

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(Document entity);
}
