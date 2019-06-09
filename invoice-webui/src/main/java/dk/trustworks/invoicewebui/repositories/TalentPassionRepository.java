package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.TalentPassion;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.TalentPassionType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by hans on 23/06/2017.
 */

@Transactional
@RepositoryRestResource(collectionResourceRel = "talentpassion", path = "talentpassions")
public interface TalentPassionRepository extends CrudRepository<TalentPassion, String> {

    Optional<TalentPassion> findByUserAndOwnerAndTypeAndRegistered(User user, User owner, TalentPassionType type, LocalDate registered);

}
