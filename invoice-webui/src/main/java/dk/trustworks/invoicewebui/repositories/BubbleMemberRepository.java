package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "bubblemembers", path="bubblemembers")
public interface BubbleMemberRepository extends CrudRepository<BubbleMember, String> {

    List<BubbleMember> findByBubble(@Param("bubble") Bubble bubble);
    List<BubbleMember> findByMember(@Param("member") User user);
    BubbleMember findByBubbleAndMember(@Param("bubble") Bubble bubble, @Param("member") User user);

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(BubbleMember entity);


}
