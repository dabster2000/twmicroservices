package dk.trustworks.processors;

import dk.trustworks.model.Client;
import dk.trustworks.model.Project;
import dk.trustworks.model.Task;
import dk.trustworks.model.Taskworkerconstraint;
import dk.trustworks.repositories.ClientRepository;
import dk.trustworks.repositories.ProjectRepository;
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
public class TaskResourceProcessor implements ResourceProcessor<Resource<Task>> {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Resource<Task> process(Resource<Task> resource) {
        System.out.println("TaskWorkerConstraintResourceProcessor.process");
        System.out.println("resource = [" + resource.getContent() + "]");
        Task task = resource.getContent();
        Project project = projectRepository.findOne(task.getProjectuuid());
        task.setProject(project);
        Client client = clientRepository.findOne(project.getClientuuid());
        project.setClient(client);
        System.out.println("resource = " + resource.getContent());
        return resource;
    }
}