package dk.trustworks.invoicewebui.services;

import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.network.rest.ProjectRestService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectService implements InitializingBean {

    private static ProjectService instance;

    private final ProjectRestService projectRestService;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRestService projectRestService, UserService userService) {
        this.projectRestService = projectRestService;
        this.userService = userService;
    }

    public List<Project> findAllByOrderByNameAsc() {
        return projectRestService.findAll();
    }

    public List<Project> findAllByActiveTrueOrderByNameAsc() {
        return projectRestService.findByActiveTrue();
    }

    public int findByWorkonCount(LocalDate fromDate, LocalDate toDate) {
        return Integer.parseInt(projectRestService.findByWorkonCount(fromDate, toDate).getValue());
    }


    public List<Project> findByClientAndActiveTrueOrderByNameAsc(String clientuuid) {
        return projectRestService.findByClientAndActiveTrue(clientuuid);
    }

    public List<Project> findByClientuuidOrderByNameAsc(String clientuuid) {
        return projectRestService.findByClientuuid(clientuuid);
    }

    public List<Project> findByClientdata(Clientdata clientdata) {
        return projectRestService.findByClientdata(clientdata);
    }

    public double findRateByProjectAndUserAndDate(String projectuuid, String useruuid, LocalDate date) {
        return projectRestService.findRate(projectuuid, useruuid, date);
    }

    public void delete(String id) {
        projectRestService.delete(id);
    }

    public void delete(Project entity) {
        projectRestService.delete(entity);
    }

    @Transactional
    public Project save(Project project) {
        project.setUserowneruuid(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser().getUuid());
        project = projectRestService.save(project);
        return project;
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
        return projectRestService.findOne(projectUUID);
    }
/*
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

 */

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ProjectService get() {
        return instance;
    }
}
