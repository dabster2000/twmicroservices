package dk.trustworks.invoicewebui.services;

import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.TaskRepository;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Project> findAllByOrderByNameAsc() {
        return projectRepository.findAllByOrderByNameAsc();
    }

    public List<Project> findAllByActiveTrueOrderByNameAsc() {
        return projectRepository.findAllByActiveTrueOrderByNameAsc();
    }

    public List<Project> findByClientAndActiveTrueOrderByNameAsc(Client client) {
        return projectRepository.findByClientAndActiveTrueOrderByNameAsc(client);
    }

    public List<Project> findByClientOrderByNameAsc(Client client) {
        return projectRepository.findByClientOrderByNameAsc(client);
    }

    public List<Project> findByClientdata(Clientdata clientdata) {
        return projectRepository.findByClientdata(clientdata);
    }

    public void delete(String id) {
        projectRepository.delete(id);
    }

    public void delete(Project entity) {
        projectRepository.delete(entity);
    }

    @Transactional
    public Project save(Project project) {
        project = projectRepository.save(project);
        project.setOwner(userService.findByUUID(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser().getUuid()));
        return createDefaultTask(project);
    }

    public Iterable<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findOne(String projectUUID) {
        return createDefaultTask(projectRepository.findOne(projectUUID));
    }

    private Project createDefaultTask(Project project) {
        boolean hasSOTypeTask = false;
        for (Task task : project.getTasks()) {
            if(task.getType().equals(TaskType.SO)) hasSOTypeTask = true;
        }
        if(!hasSOTypeTask) {
            Task task = taskRepository.save(new Task("Ikke fakturerbar", project, TaskType.SO));
            project.getTasks().add(task);
        }
        return project;
    }
}
