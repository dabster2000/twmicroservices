package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.repositories.TaskworkerconstraintRepository;
import dk.trustworks.invoicewebui.repositories.UserStatusRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DashboardBoxCreator {

    private final UserStatusRepository userStatusRepository;

    private final WorkRepository workRepository;

    private final TaskworkerconstraintRepository taskworkerconstraintRepository;

    private final GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    public DashboardBoxCreator(UserStatusRepository userStatusRepository, WorkRepository workRepository, TaskworkerconstraintRepository taskworkerconstraintRepository, GraphKeyValueRepository graphKeyValueRepository) {
        this.userStatusRepository = userStatusRepository;
        this.workRepository = workRepository;
        this.taskworkerconstraintRepository = taskworkerconstraintRepository;
        this.graphKeyValueRepository = graphKeyValueRepository;
    }

    @Cacheable("goodpeople")
    public TopCardContent getGoodPeopleBox() {
        // TODO: Count instead of load: https://stackoverflow.com/questions/37569467/spring-data-jpa-get-the-values-of-a-non-entity-column-of-a-custom-native-query
        float goodPeopleNow = userStatusRepository.findAllActive().size();
        String date = LocalDate.now().minusYears(1).toString("yyyy-MM-dd");
        // TODO: Count instead of load
        float goodPeopleLastYear = userStatusRepository.findAllActiveByDate(date).size();
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        return new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Good People", percent + "% more than last year", Math.round(goodPeopleNow)+"", "medium-blue");
    }

    @Cacheable("activeprojects")
    public TopCardContent createActiveProjectsBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        Map<String, Project> currentProjectSet = new HashMap<>();
        Map<String, Project> noProjectSet = new HashMap<>();
        for (Work work : workRepository.findByPeriod(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            } else {
                noProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }

        Map<String, Project> lastProjectSet = new HashMap<>();
        for (Work work : workRepository.findByPeriod(lastStartDate.toString("yyyy-MM-dd"), lastEndDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
        float projectsThisYear = currentProjectSet.size();
        int projectsLastYear = lastProjectSet.size();
        int percentProjects = Math.round((projectsThisYear / projectsLastYear) * 100) - 100;
        String projectsMoreOrLess = "more";
        if(percentProjects < 0) projectsMoreOrLess = "less";
        percentProjects = Math.abs(percentProjects);
        return new TopCardContent("images/icons/ic_date_range_48pt_2x.png", "Active Projects", percentProjects+"% "+projectsMoreOrLess+" than last year", ""+currentProjectSet.size(), "dark-green");
    }

    @Cacheable("billablehours")
    public TopCardContent createBillableHoursBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        Map<String, Project> currentProjectSet = new HashMap<>();
        Map<String, Project> noProjectSet = new HashMap<>();
        float billableHoursThisYear = 0f;
        for (Work work : workRepository.findByPeriod(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                billableHoursThisYear += work.getWorkduration();
                currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            } else {
                noProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }

        Map<String, Project> lastProjectSet = new HashMap<>();
        float billableHoursLastYear = 0f;
        for (Work work : workRepository.findByPeriod(lastStartDate.toString("yyyy-MM-dd"), lastEndDate.toString("yyyy-MM-dd"))) {
            Task task = work.getTask();
            User user = work.getUser();
            List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTaskAndUser(task, user);
            if(taskworkerconstraints.size()>0 && taskworkerconstraints.get(0).getPrice() > 0 && work.getWorkduration() > 0) {
                billableHoursLastYear += work.getWorkduration();
                lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
        int percentBillableHours = Math.round((billableHoursThisYear / billableHoursLastYear) * 100) - 100;
        String hoursMoreOrLess = "more";
        if(percentBillableHours < 0) hoursMoreOrLess = "less";
        return new TopCardContent("images/icons/ic_access_time_48pt_2x.png", "Billable Hours", percentBillableHours+"% "+hoursMoreOrLess+" than last year", ""+Math.round(billableHoursThisYear), "orange");
    }

    @Cacheable("consultantsperproject")
    public TopCardContent createConsultantsPerProjectBox() {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.countConsultantsPerProject(startDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"));
        double numberOfConsultants = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemList) {
            numberOfConsultants += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProject = (double) Math.round((numberOfConsultants / amountPerItemList.size()) * 100) / 100;

        List<GraphKeyValue> amountPerItemListOld = graphKeyValueRepository.countConsultantsPerProject(lastStartDate.toString("yyyyMMdd"), lastEndDate.toString("yyyyMMdd"));
        double numberOfConsultantsOld = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemListOld) {
            numberOfConsultantsOld += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProjectOld = (double) Math.round((numberOfConsultantsOld / amountPerItemListOld.size()) * 100) / 100;

        String numberOfConsultantsMoreOrLess = "more";
        if(numberOfConsultantsPerProject < numberOfConsultantsPerProjectOld) numberOfConsultantsMoreOrLess = "less";

        double percentNumberOfConsultantsPerProject = Math.round((numberOfConsultantsPerProject / numberOfConsultantsPerProjectOld) * 100) - 100;

        TopCardDesign consultantsCard4 = new TopCardDesign();
        consultantsCard4.getImgIcon().setSource(new ThemeResource("images/icons/ic_people_black_48dp_2x.png"));
        consultantsCard4.getLblNumber().setValue(""+numberOfConsultantsPerProject);
        consultantsCard4.getLblTitle().setValue("Consultants per Project");
        consultantsCard4.getLblSubtitle().setValue(percentNumberOfConsultantsPerProject+"% "+numberOfConsultantsMoreOrLess+" than last year");
        consultantsCard4.getCardHolder().addStyleName("dark-grey");
        return new TopCardContent("images/icons/ic_people_black_48dp_2x.png", "Consultants per Project", percentNumberOfConsultantsPerProject+"% "+numberOfConsultantsMoreOrLess+" than last year", ""+numberOfConsultantsPerProject, "dark-grey");
    }

}
