package dk.trustworks.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.model.Task;
import dk.trustworks.model.Taskworkerconstraint;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "taskworkerconstraints", path="taskworkerconstraints")
public interface TaskworkerconstraintRepository extends PagingAndSortingRepository<Taskworkerconstraint, String> {
    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Taskworkerconstraint entity);
}
