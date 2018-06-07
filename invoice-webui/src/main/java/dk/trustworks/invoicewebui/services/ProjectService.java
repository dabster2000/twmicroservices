package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
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

    public Project save(Project project) {
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
            Task task = taskRepository.save(new Task("Satans os'e", project, TaskType.SO));
            project.getTasks().add(task);
        }
        return projectRepository.save(project);
    }
}
