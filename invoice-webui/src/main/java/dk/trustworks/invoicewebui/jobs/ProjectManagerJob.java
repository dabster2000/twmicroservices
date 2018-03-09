package dk.trustworks.invoicewebui.jobs;

import com.google.common.hash.Hashing;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 12/09/2017.
 */

@Component
public class ProjectManagerJob {

    private static final Logger log = LoggerFactory.getLogger(ProjectManagerJob.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    //@Scheduled(cron = "0 30 23 * * MON-FRI")
    @Scheduled(fixedRate = 12000)
    public void reportCurrentTime() {
        LocalDate dateThreeMonthsAgo = LocalDate.now().minusMonths(3);
        for (Project project : projectRepository.findAllByActiveTrueOrderByNameAsc()) {
            String sha512hex = Hashing.sha512().hashString(project.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
            if(newsRepository.findFirstBySha512(sha512hex).size()>0) continue;
            boolean projectHasWork = false;
            taskLoop:
            for (Task task : project.getTasks()) {
                for (Work work : task.getWorkList()) {
                    LocalDate workDate = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay());
                    if(workDate.isAfter(dateThreeMonthsAgo)) {
                        projectHasWork = true;
                        break taskLoop;
                    }
                }
            }
            if(!projectHasWork) {
                log.info("Project should close: "+project);
                newsRepository.save(new News(
                        "No one is working on the project  '"+project.getName()+"'. Consider making it inactive.",
                        LocalDate.now(),
                        "project", ProjectManagerView.VIEW_NAME+"/"+project.getUuid(),
                        sha512hex
                ));
            }
        }
    }

    @Transactional
    @Scheduled(fixedRate = 100000)
    public void reportEndingProjects() {
        List<Project> projects = projectRepository.findAllByActiveTrueOrderByNameAsc();
        for (Project project : projects) {
            if(project.getStartdate().isAfter(LocalDate.now().minusMonths(1))) {
                String sha512hex = Hashing.sha512().hashString(project.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
                if(newsRepository.findFirstBySha512(sha512hex).size()>0) continue;
                String consultants = "";
                for (Task task : project.getTasks()) {
                    for (Taskworkerconstraint taskworkerconstraint : task.getTaskworkerconstraint()) {
                        if(taskworkerconstraint.getPrice() >= 1.0) continue;
                        consultants += taskworkerconstraint.getUser().getFirstname() + " " + taskworkerconstraint.getUser().getLastname() + ", ";
                    }
                }
                newsRepository.save(new News(
                        "We have started an exciting new project recently! " +
                                "Its called '"+project.getName()+"' and its for our client "+project.getClient().getName()+". " +
                                "From what have been announced, "+consultants+" have been assigned as consultants.",
                        project.getStartdate(),
                        "project", ProjectManagerView.VIEW_NAME+"/"+project.getUuid(),
                        sha512hex
                ));
            }
            if(project.getEnddate().isBefore(LocalDate.now().plusMonths(1)) && project.getEnddate().isAfter(LocalDate.now())) {
                String sha512hex = Hashing.sha512().hashString(project.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
                if(newsRepository.findFirstBySha512(sha512hex).size()>0) continue;
                String consultants = "";
                for (Task task : project.getTasks()) {
                    for (Taskworkerconstraint taskworkerconstraint : task.getTaskworkerconstraint()) {
                        if(taskworkerconstraint.getPrice() >= 1.0) continue;
                        consultants += taskworkerconstraint.getUser().getFirstname() + " " + taskworkerconstraint.getUser().getLastname() + ", ";
                    }
                }
                newsRepository.save(new News(
                        "The project '"+project.getName()+"' " +
                                "for our client "+project.getClient().getName()+" is ending soon. " +
                                "Your colleagues "+consultants+" will be available for other tasks.",
                        project.getEnddate(),
                        "project", ProjectManagerView.VIEW_NAME+"/"+project.getUuid(),
                        sha512hex
                ));
            }
        }
    }
}