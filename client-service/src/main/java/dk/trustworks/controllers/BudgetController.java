package dk.trustworks.controllers;

import dk.trustworks.model.Budget;
import dk.trustworks.model.Client;
import dk.trustworks.model.Clientdata;
import dk.trustworks.model.Task;
import dk.trustworks.repositories.BudgetRepository;
import dk.trustworks.repositories.ClientRepository;
import dk.trustworks.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 14/08/2017.
 */

@Transactional
@RepositoryRestController
public class BudgetController {

    @Autowired
    BudgetRepository budgetRepository;

    @Autowired
    TaskRepository taskRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/budgets/batch", method = RequestMethod.POST)
    public void createClientdata(@RequestBody List<Budget> budgets) {
        for (Budget budget : budgets) {
            Task task = taskRepository.findOne(budget.getTaskuuid());
            budget.setCreated(Timestamp.from(Instant.now()));
            budget.setUuid(UUID.randomUUID().toString());
            budget.setTask(task);
            budgetRepository.save(budget);
        }
    }
}
