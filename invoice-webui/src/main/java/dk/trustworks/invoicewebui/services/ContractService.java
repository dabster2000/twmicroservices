package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.MainContract;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Taskworkerconstraint;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.MainContractRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.SubContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContractService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MainContractRepository mainContractRepository;

    @Autowired
    private SubContractRepository subContractRepository;

    public MainContract createContract(MainContract mainContract) {
        return mainContractRepository.save(mainContract);
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

    public Taskworkerconstraint findByWork(Work work) {

        return null;
    }

    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}