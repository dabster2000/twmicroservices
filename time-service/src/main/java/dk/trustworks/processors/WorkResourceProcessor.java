package dk.trustworks.processors;

import dk.trustworks.model.Week;
import dk.trustworks.model.Work;
import dk.trustworks.model.dto.User;
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
import java.util.function.Consumer;

/**
 * Created by hans on 28/06/2017.
 */
@Component
public class WorkResourceProcessor implements ResourceProcessor<Resource<Work>> {

    @Autowired
    @Qualifier("userResource")
    private DiscoveredResource userByUUIDResource;

    @Autowired
    @Qualifier("taskResource")
    private DiscoveredResource taskByUUIDResource;

    @Override
    public Resource<Work> process(Resource<Work> resource) {
        Work work = resource.getContent();
        String useruuid = work.getUseruuid();
        String taskuuid = work.getTaskuuid();

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
        });

        return resource;
    }
}