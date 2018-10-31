package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.ProjectDescription;
import dk.trustworks.invoicewebui.model.ProjectDescriptionUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "projectdescription_users", path="projectdescription_users")
public interface ProjectDescriptionUserRepository extends CrudRepository<ProjectDescriptionUser, Integer> {

    List<ProjectDescriptionUser> findByProjectDescription(@Param("projectdescription") ProjectDescription projectdescription);

    @Transactional
    //@Modifying
    //@Query(value = "DELETE FROM projectdescription_users WHERE projectdescid = :projectdescid", nativeQuery = true)
    void deleteByProjectDescription(@Param("projectdescription") ProjectDescription projectdescription);

}
