package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.dto.UserAmbitionDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "user_ambitions", path="user_ambitions")
public interface UserAmbitionDTORepository extends CrudRepository<UserAmbitionDTO, Integer> {

    @Query(value = "select ua.id, a.name as name, ua.score as score, ua.ambition as ambition from user_ambition ua left join ambition a on ua.ambitionid = a.id where a.category LIKE :category and ua.useruuid LIKE :useruuid and a.active = 1 ", nativeQuery = true)
    List<UserAmbitionDTO> findUserAmbitionByUseruuidAndCategoryAndActiveTrue(@Param("useruuid") String useruuid, @Param("category") String category);

    @Query(value = "select ua.id, a.name as name, ua.score as score, ua.ambition as ambition from user_ambition ua left join ambition a on ua.ambitionid = a.id where ua.useruuid LIKE :useruuid and a.active = 1 ", nativeQuery = true)
    List<UserAmbitionDTO> findUserAmbitionByUseruuidAndCategoryAndActiveTrue(@Param("useruuid") String useruuid);

    @Override @RestResource(exported = false) void delete(Integer id);
    @Override @RestResource(exported = false) void delete(UserAmbitionDTO entity);

}
