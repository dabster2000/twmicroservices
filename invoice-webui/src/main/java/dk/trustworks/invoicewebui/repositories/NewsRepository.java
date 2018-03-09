package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.News;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "news",path="news")
public interface NewsRepository extends CrudRepository<News, String> {

    List<News> findFirstBySha512(@Param("sha512") String sha512);
    List<News> findTop10ByOrderByNewsdateDesc();

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(News entity);
}
