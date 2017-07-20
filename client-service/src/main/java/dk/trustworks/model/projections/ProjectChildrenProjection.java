package dk.trustworks.model.projections;

import dk.trustworks.model.Client;
import dk.trustworks.model.Project;
import dk.trustworks.model.Task;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * Created by hans on 25/06/2017.
 */
@Projection(name = "children", types = { Project.class })
public interface ProjectChildrenProjection {

    String getName();

    List<Task> getTasks();

}
