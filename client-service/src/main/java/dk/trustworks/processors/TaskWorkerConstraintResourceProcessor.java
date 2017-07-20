package dk.trustworks.processors;

import dk.trustworks.model.Taskworkerconstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by hans on 28/06/2017.
 */
@Component
public class TaskWorkerConstraintResourceProcessor implements ResourceProcessor<Resource<Taskworkerconstraint>> {

    @Autowired
    @Qualifier("userResource")
    private DiscoveredResource userByUUIDResource;

    @Override
    public Resource<Taskworkerconstraint> process(Resource<Taskworkerconstraint> resource) {
        Taskworkerconstraint taskworkerconstraint = resource.getContent();
        String useruuid = taskworkerconstraint.getUseruuid();

        Optional<Link> userlink = Optional.ofNullable(userByUUIDResource.getLink());

        userlink.ifPresent(it -> {
            if (useruuid == null) {
                return;
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("uuid", useruuid);
            resource.add(it.expand(parameters).withRel("user"));
        });

        return resource;
    }
}