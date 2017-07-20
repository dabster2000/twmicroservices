package dk.trustworks.controllers;

import dk.trustworks.model.Taskworkerconstraint;
import dk.trustworks.network.UserClient;
import dk.trustworks.repositories.TaskworkerconstraintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hans on 28/06/2017.
 */

@RepositoryRestController
public class RemoteUserController {

    private final TaskworkerconstraintRepository taskworkerconstraintRepository;

    @Autowired
    public RemoteUserController(TaskworkerconstraintRepository taskworkerconstraintRepository) {
        this.taskworkerconstraintRepository = taskworkerconstraintRepository;
    }
/*

    @RequestMapping(method = GET, value = "/taskworkerconstraints/users")
    public @ResponseBody ResponseEntity<?> getUsers() {
        Taskworkerconstraint taskworkerconstraints = taskworkerconstraintRepository.findAll();

        Resource<Taskworkerconstraint> resource = new Resource<>(taskworkerconstraints);


        resource.add(linkTo(methodOn(RemoteUserController.class).getUsers()).withSelfRel());

        // add other links as needed

        return ResponseEntity.ok(resources);
    }
    */
}
