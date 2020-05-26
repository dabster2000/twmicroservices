package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.network.rest.TaskRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService implements InitializingBean {

    private static TaskService instance;

    private final TaskRestService taskRestService;

    @Autowired
    public TaskService(TaskRestService taskRestService) {
        this.taskRestService = taskRestService;
    }

    public Task findOne(String uuid) {
        return taskRestService.findOne(uuid);
    }

    public List<Task> findByProject(String projectuuid) {
        return taskRestService.findByProject(projectuuid);
    }


    public void delete(String id) {
        taskRestService.delete(id);
    }

    public void delete(Task entity) {
        taskRestService.delete(entity);
    }

    public Task save(Task task) {
        return taskRestService.save(task);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static TaskService get() {
        return instance;
    }
}
