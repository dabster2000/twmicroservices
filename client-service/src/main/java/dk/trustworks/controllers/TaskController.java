package dk.trustworks.controllers;

import dk.trustworks.model.Client;
import dk.trustworks.model.Project;
import dk.trustworks.model.Task;
import dk.trustworks.model.TaskWithExpandedName;
import dk.trustworks.repositories.ClientRepository;
import dk.trustworks.repositories.ProjectRepository;
import dk.trustworks.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hans on 14/08/2017.
 */

@RepositoryRestController
public class TaskController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TaskRepository taskRepository;

    @RequestMapping(value = "/tasks/parents", method = GET)
    public @ResponseBody ResponseEntity<?> findAll(@RequestParam("uuid") String uuid) {
        System.out.println("TaskController.findAll");
        System.out.println("uuid = [" + uuid + "]");
        Task task = taskRepository.findOne(uuid);
        Project project = projectRepository.findOne(task.getProjectuuid());
        task.setName(project.getName() + " / " + task.getName());
        //Client client = clientRepository.findOne(project.getClientuuid());
        return ResponseEntity.ok(new Resource<>(task));
    }

    @Transactional
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public void createTask(@RequestBody Task task) {
        Project project = projectRepository.findOne(task.getProjectuuid());
        task.setProject(project);
        task.setUuid(UUID.randomUUID().toString());
        taskRepository.save(task);
    }
}
