package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.ContractRepository;
import dk.trustworks.invoicewebui.repositories.MainContractRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ProjectRepository projectRepository;

    private final MainContractRepository mainContractRepository;

    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ProjectRepository projectRepository, MainContractRepository mainContractRepository, ContractRepository contractRepository) {
        this.projectRepository = projectRepository;
        this.mainContractRepository = mainContractRepository;
        this.contractRepository = contractRepository;
    }

    public MainContract createContract(MainContract mainContract) {
        return mainContractRepository.save(mainContract);
    }

    public void updateContract(MainContract contract) {
        contractRepository.save(contract);
    }

    public void updateContract(SubContract contract) {
        contractRepository.save(contract);
    }

    public MainContract addProjects(MainContract mainContract, Set<Project> projects) throws ContractValidationException {
        // validate
        for (Project project : projects) {
            for (MainContract contract : project.getMainContracts()) {
                Set<Object> userUUIDs = contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
                if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), mainContract.getActiveFrom(), mainContract.getActiveTo()) &&
                        userUUIDs.contains(mainContract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
                    throw new ContractValidationException("Overlapping another contract with same consultants");
            }
        }

        // execute
        for (Project project : projects) {
            project.addMainContract(mainContract);
            projectRepository.save(project);
        }
        mainContract.addProjects(projects);
        mainContractRepository.save(mainContract);
        return mainContract;
    }

    public Double findConsultantRateByWork(Work work) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        Double rate = contractRepository.findConsultantRateByWork(work.getYear() + "-" + (work.getMonth()+1) + "-" + work.getDay(), work.getUser().getUuid(), work.getTask().getUuid());
        return rate;
    }

    public Double findConsultantRate(int year, int month, int day, User user, Task task) {
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        Double rate = contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), user.getUuid());
        return rate;
    }

    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}