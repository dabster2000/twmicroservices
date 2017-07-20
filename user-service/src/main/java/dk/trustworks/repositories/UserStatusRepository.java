package dk.trustworks.repositories;

import dk.trustworks.model.Salary;
import dk.trustworks.model.User;
import dk.trustworks.model.UserStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by hans on 23/06/2017.
 */

@RepositoryRestResource(collectionResourceRel = "statuses", path = "statuses")
public interface UserStatusRepository extends PagingAndSortingRepository<UserStatus, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(UserStatus entity);
}
