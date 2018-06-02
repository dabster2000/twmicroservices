package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.MainContractRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConvertOldToNew {


    private static final Logger log = LoggerFactory.getLogger(BudgetCleanupJob.class);

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MainContractRepository mainContractRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Transactional
    //@Scheduled(fixedDelay = 1000000, initialDelay = 1000)
    public void job() {
        log.info("running work job...");

        for (Project project : projectRepository.findAll()) {
            log.info("Converting project to contract = " + project);
            Client client = project.getClient();
            if(client.getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) continue;
            MainContract mainContract = new MainContract(ContractType.AMOUNT, ContractStatus.SIGNED, "", project.getStartdate(), project.getEnddate(), project.getBudget(), client);
            mainContract.addProject(project);
            System.out.println("project.getTasks() = " + project.getTasks());
            if(project.getTasks()==null) {
                System.out.println("project.getTasks() = " + project.getTasks());
                continue;
            }
            for (Task task : project.getTasks()) {
                for (Taskworkerconstraint constraint : task.getTaskworkerconstraint()) {
                    Double rate = constraint.getPrice();
                    User user = constraint.getUser();
                    Consultant consultant = new Consultant(mainContract, user, rate, 0.0, 0.0);
                    mainContract.addConsultant(consultant);
                    consultant.setMainContract(mainContract);
                }
            }
            project.addMainContract(mainContract);
            mainContractRepository.save(mainContract);
        }

        Map<String, Work> noContract = new HashMap<>();
        List<Work> workNotFoundList = new ArrayList<>();
        List<Work> workOutsidePeriod = new ArrayList<>();
        log.info("Checking all registered work...");
        for (Work work : workRepository.findByPeriod("2014-01-01", "2019-12-31")) {
            if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) continue;
            if(!(work.getWorkduration()>0.0)) continue;
            boolean foundPrice = false;
            boolean outsidePeriod = false;
            Project project = work.getTask().getProject();
            LocalDate workDate = LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay());
            for (MainContract mainContract : project.getMainContracts()) {
                if(mainContract.findByUser(work.getUser())!=null) {
                    outsidePeriod = true;
                    if (workDate.isAfter(mainContract.getActiveFrom()) &&
                            workDate.isBefore(mainContract.getActiveTo())) {
                        foundPrice = true;
                        outsidePeriod = false;
                    }
                }
            }
            if(!foundPrice && !outsidePeriod) {
                workNotFoundList.add(work);
                noContract.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(), work);
            }
            if(outsidePeriod) workOutsidePeriod.add(work);
        }

        log.info("Printing work not found... "+workNotFoundList.size());
        for (Work work : workNotFoundList) {
            if(!work.getTask().getProject().getClient().getName().equals("TrustWorks")) ;
                System.out.println("[client: " + work.getTask().getProject().getClient().getName() + ", project: "+work.getTask().getProject().getName() + ", user: "+work.getUser().getUsername()+"]");
        }

        log.info("Printing work found outside period... "+workOutsidePeriod.size());
        for (Work work : workOutsidePeriod) {
            if(!work.getTask().getProject().getClient().getName().equals("TrustWorks"));
                //System.out.println("[client: " + work.getTask().getProject().getClient().getName() + ", project: "+work.getTask().getProject().getName() + ", user: "+work.getUser().getUsername()+", work date: "+LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay())+"]");
        }

        log.info("Printing missing contract... "+noContract.values().size());
        for (Work work : noContract.values()) {
            if(!work.getTask().getProject().getClient().getName().equals("TrustWorks")) ;
            System.out.println("[client: " + work.getTask().getProject().getClient().getName() + ", project: "+work.getTask().getProject().getName() + ", user: "+work.getUser().getUsername()+"]");
        }

    }
}
