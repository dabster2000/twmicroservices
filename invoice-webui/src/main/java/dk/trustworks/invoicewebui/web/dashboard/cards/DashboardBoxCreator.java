package dk.trustworks.invoicewebui.web.dashboard.cards;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.repositories.UserStatusRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DashboardBoxCreator {

    private final UserStatusRepository userStatusRepository;

    private final WorkService workService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ContractService contractService;

    @Autowired
    public DashboardBoxCreator(UserStatusRepository userStatusRepository, WorkService workService, GraphKeyValueRepository graphKeyValueRepository, ContractService contractService) {
        this.userStatusRepository = userStatusRepository;
        this.workService = workService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.contractService = contractService;
    }

    @Cacheable("goodpeople")
    public TopCardContent getGoodPeopleBox() {
        // TODO: Count instead of load: https://stackoverflow.com/questions/37569467/spring-data-jpa-get-the-values-of-a-non-entity-column-of-a-custom-native-query
        float goodPeopleNow = userStatusRepository.findAllActive().size();
        String date = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // TODO: Count instead of load
        float goodPeopleLastYear = userStatusRepository.findAllActiveByDate(date).size();
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        return new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Good People", percent + "% more than last year", Math.round(goodPeopleNow)+"", "dark-blue");
    }

    @Cacheable("activeprojects")
    public TopCardContent createActiveProjectsBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        Map<String, Project> currentProjectSet = new HashMap<>();
        //Map<String, Project> noProjectSet = new HashMap<>();
        for (Work work : workService.findByPeriod(startDate, endDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            } /*else {
                noProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }*/
        }

        Map<String, Project> lastProjectSet = new HashMap<>();
        for (Work work : workService.findByPeriod(lastStartDate, lastEndDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
        float projectsThisYear = currentProjectSet.size();
        int projectsLastYear = lastProjectSet.size();
        int percentProjects = Math.round((projectsThisYear / projectsLastYear) * 100) - 100;
        String projectsMoreOrLess = "more";
        if(percentProjects < 0) projectsMoreOrLess = "less";
        percentProjects = Math.abs(percentProjects);
        return new TopCardContent("images/icons/trustworks_icon_kalender.svg", "Active Projects", percentProjects+"% "+projectsMoreOrLess+" than last year", ""+currentProjectSet.size(), "dark-blue");
    }

    @Cacheable("billablehours")
    public TopCardContent createBillableHoursBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        //Map<String, Project> currentProjectSet = new HashMap<>();
        //Map<String, Project> noProjectSet = new HashMap<>();
        float billableHoursThisYear = 0f;
        for (Work work : workService.findByPeriod(startDate, endDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                billableHoursThisYear += work.getWorkduration();
                //currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            } //else {
                //noProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            //}
        }

        //Map<String, Project> lastProjectSet = new HashMap<>();
        float billableHoursLastYear = 0f;
        for (Work work : workService.findByPeriod(lastStartDate, lastEndDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                billableHoursLastYear += work.getWorkduration();
                //lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
        int percentBillableHours = Math.round((billableHoursThisYear / billableHoursLastYear) * 100) - 100;
        String hoursMoreOrLess = "more";
        if(percentBillableHours < 0) hoursMoreOrLess = "less";
        return new TopCardContent("images/icons/trustworks_icon_ur.svg", "Billable Hours", percentBillableHours+"% "+hoursMoreOrLess+" than last year", ""+Math.round(billableHoursThisYear), "dark-blue");
    }

    @Cacheable("consultantsperproject")
    public TopCardContent createConsultantsPerProjectBox() {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.countConsultantsPerProject(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        double numberOfConsultants = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemList) {
            numberOfConsultants += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProject = (double) Math.round((numberOfConsultants / amountPerItemList.size()) * 100) / 100;

        List<GraphKeyValue> amountPerItemListOld = graphKeyValueRepository.countConsultantsPerProject(lastStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), lastEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        double numberOfConsultantsOld = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemListOld) {
            numberOfConsultantsOld += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProjectOld = (double) Math.round((numberOfConsultantsOld / amountPerItemListOld.size()) * 100) / 100;

        String numberOfConsultantsMoreOrLess = "more";
        if(numberOfConsultantsPerProject < numberOfConsultantsPerProjectOld) numberOfConsultantsMoreOrLess = "less";

        double percentNumberOfConsultantsPerProject = Math.round((numberOfConsultantsPerProject / numberOfConsultantsPerProjectOld) * 100) - 100;

        /*
        TopCardDesign consultantsCard4 = new TopCardDesign();
        consultantsCard4.getImgIcon().setSource(new ThemeResource("images/icons/ic_people_black_48dp_2x.png"));
        consultantsCard4.getLblNumber().setValue(""+numberOfConsultantsPerProject);
        consultantsCard4.getLblTitle().setValue("Consultants per Project");
        consultantsCard4.getLblSubtitle().setValue(percentNumberOfConsultantsPerProject+"% "+numberOfConsultantsMoreOrLess+" than last year");
        consultantsCard4.getCardHolder().addStyleName("dark-grey");
        */
        return new TopCardContent("images/icons/trustworks_icon_gruppe.svg", "Consultants per Project", percentNumberOfConsultantsPerProject+"% "+numberOfConsultantsMoreOrLess+" than last year", ""+numberOfConsultantsPerProject, "dark-blue");
    }

}
