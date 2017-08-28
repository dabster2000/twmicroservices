package dk.trustworks.processors;

import dk.trustworks.model.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
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
public class WeekResourceProcessor implements ResourceProcessor<Resource<Week>> {

    @Autowired
    @Qualifier("userResource")
    private DiscoveredResource userByUUIDResource;

    @Autowired
    @Qualifier("taskResource")
    private DiscoveredResource taskByUUIDResource;

    @Override
    public Resource<Week> process(Resource<Week> resource) {
        Week week = resource.getContent();
        String useruuid = week.getUseruuid();
        String taskuuid = week.getTaskuuid();

        Optional<Link> userlink = Optional.ofNullable(userByUUIDResource.getLink());
        Optional<Link> tasklink = Optional.ofNullable(taskByUUIDResource.getLink());

        userlink.ifPresent(it -> {
            if (useruuid == null) {
                return;
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("uuid", useruuid);
            resource.add(it.expand(parameters).withRel("user"));
        });

        tasklink.ifPresent(it -> {
            if (taskuuid == null) {
                return;
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("uuid", taskuuid);
            resource.add(it.expand(parameters).withRel("task"));

            Map<String, Object> parameters2 = new HashMap<>();
            parameters2.put("uuid", taskuuid);
            parameters2.put("parents", "true");
            resource.add(it.expand(parameters2).withRel("task-parents"));
        });

        return resource;
    }
}