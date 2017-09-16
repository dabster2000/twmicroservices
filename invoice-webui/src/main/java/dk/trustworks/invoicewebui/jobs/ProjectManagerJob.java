package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hans on 12/09/2017.
 */

@Component
public class ProjectManagerJob {

    private static final Logger log = LoggerFactory.getLogger(ProjectManagerJob.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 1 1/1 *")
    public void reportCurrentTime() {
        LocalDate dateThreeMonthsAgo = LocalDate.now().minusMonths(3);
        for (Project project : projectRepository.findAllByActiveTrueOrderByNameAsc()) {
            boolean projectHasWork = false;
            taskLoop:
            for (Task task : project.getTasks()) {
                for (Work work : task.getWorkList()) {
                    LocalDate workDate = new LocalDate(work.getYear(), work.getMonth()+1, work.getDay());
                    if(workDate.isAfter(dateThreeMonthsAgo)) {
                        projectHasWork = true;
                        break taskLoop;
                    }
                }
            }
            if(!projectHasWork) {
                log.info("Project should close: "+project);
                notificationRepository.save(new Notification(
                        project.getOwner(),
                        LocalDateTime.now().withDayOfMonth(30).toDate(),
                        "Project expired",
                        "No work has been done on the project '"+project.getName()+"'. Consider making it inactive.",
                        ProjectManagerView.VIEW_NAME+"/"+project.getUuid()));
                //Broadcaster.broadcast("notification");
            }
        }
    }
}
