package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Achievement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/*
@RepositoryRestResource(collectionResourceRel = "achievements", path="achievements")
public interface AchievementRepository extends CrudRepository<Achievement, Integer> {

    List<Achievement> findByUseruuid(@Param("user") String user);




}

 */
