package dk.trustworks.controllers;

import dk.trustworks.model.Budget;
import dk.trustworks.model.Project;
import dk.trustworks.model.Task;
import dk.trustworks.repositories.BudgetRepository;
import dk.trustworks.repositories.ProjectRepository;
import dk.trustworks.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 14/08/2017.
 */

@Transactional
@RepositoryRestController
public class TaskController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TaskRepository taskRepository;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public void createTask(@RequestBody Task task) {
        Project project = projectRepository.findOne(task.getProjectuuid());
        task.setProject(project);
        task.setUuid(UUID.randomUUID().toString());
        taskRepository.save(task);
    }
}
