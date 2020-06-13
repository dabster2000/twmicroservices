package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelockProjectsJob {

    @Autowired
    private ProjectService projectService;
/*
    @Scheduled(cron = "0 0 6 * * *")
    private void execute() {
        List<Project> allProjects = projectService.findByLocked(false);
        for (Project project : allProjects) {
            project.setLocked(true);
        }
        projectService.saveAll(allProjects);
    }

 */
}
