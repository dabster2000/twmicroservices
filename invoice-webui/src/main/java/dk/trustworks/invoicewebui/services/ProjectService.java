package dk.trustworks.invoicewebui.services;

import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.network.rest.ProjectRestService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService implements InitializingBean {

    private static ProjectService instance;

    private final ProjectRestService projectRestService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRestService projectRestService, TaskService taskService, UserService userService) {
        this.projectRestService = projectRestService;
        this.taskService = taskService;
        this.userService = userService;
    }

    public List<Project> findAllByOrderByNameAsc() {
        return projectRestService.findAll();
    }

    public List<Project> findAllByActiveTrueOrderByNameAsc() {
        return projectRestService.findByActiveTrue();
    }

    public List<Project> findByClientAndActiveTrueOrderByNameAsc(Client client) {
        return projectRestService.findByClientAndActiveTrue(client);
    }

    public List<Project> findByClientOrderByNameAsc(Client client) {
        return projectRestService.findByClient(client);
    }

    public List<Project> findByClientdata(Clientdata clientdata) {
        return projectRestService.findByClientdata(clientdata);
    }

    public void delete(String id) {
        projectRestService.delete(id);
    }

    public void delete(Project entity) {
        projectRestService.delete(entity);
    }

    @Transactional
    public Project save(Project project) {
        project.setOwner(userService.findByUUID(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser().getUuid()));
        project = projectRestService.save(project);
        return createDefaultTask(project);
    }

    public void update(Project project) {
        projectRestService.update(project);
    }

    @Transactional
    public void saveAll(List<Project> projects) {
        projectRestService.save(projects);
    }

    public List<Project> findByLocked(boolean isLocked) {
        return projectRestService.findByLocked(isLocked);
    }

    public Project findOne(String projectUUID) {
        return createDefaultTask(projectRestService.findOne(projectUUID));
    }

    private Project createDefaultTask(Project project) {
        boolean hasSOTypeTask = false;
        for (Task task : taskService.findByProject(project.getUuid())) {
            if (task.getType().equals(TaskType.SO)) {
                hasSOTypeTask = true;
                break;
            }
        }
        if(!hasSOTypeTask) {
            taskService.save(new Task("Ikke fakturerbar", project, TaskType.SO));
        }
        return project;
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ProjectService get() {
        return instance;
    }
}
