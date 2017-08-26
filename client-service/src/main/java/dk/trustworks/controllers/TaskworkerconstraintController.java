package dk.trustworks.controllers;

import dk.trustworks.model.Project;
import dk.trustworks.model.Task;
import dk.trustworks.model.Taskworkerconstraint;
import dk.trustworks.repositories.ProjectRepository;
import dk.trustworks.repositories.TaskRepository;
import dk.trustworks.repositories.TaskworkerconstraintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Created by hans on 14/08/2017.
 */

@Transactional
@RepositoryRestController
public class TaskworkerconstraintController {

    @Autowired
    TaskworkerconstraintRepository taskworkerconstraintRepository;

    @Autowired
    TaskRepository taskRepository;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/taskworkerconstraints", method = RequestMethod.POST)
    public void createTask(@RequestBody Taskworkerconstraint taskworkerconstraint) {
        Task task = taskRepository.findOne(taskworkerconstraint.getTaskuuid());
        taskworkerconstraint.setTask(task);
        taskworkerconstraint.setUuid(UUID.randomUUID().toString());
        taskworkerconstraintRepository.save(taskworkerconstraint);
    }
}
