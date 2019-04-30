package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.Contract;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.trustworks.invoicewebui.utils.DateUtils.getLastDayOfMonth;

public class WorkDocumentsContainer {

    private final List<WorkDocument> workDocumentList;
    private final Map<String, WorkDocument> workDocumentMap;

    public WorkDocumentsContainer() {
        workDocumentList = new ArrayList<>();
        workDocumentMap = new HashMap<>();
    }

    public void addContract(Contract contract) {
        /*
        List<WorkDocument> existingWorkDocuments = workDocumentList.stream().filter(workDocument ->
                workDocument.getMonth().isAfter(contract.getActiveFrom().withDayOfMonth(1).minusDays(1)) &&
                        workDocument.getMonth().isBefore(contract.getActiveTo().withDayOfMonth(2)) &&
                        contract.getContractConsultants().stream().anyMatch(contractConsultant -> contractConsultant.getUser().getUuid().equals(workDocument.getUserId())) &&
                        contract.getProjects().stream().flatMap(project -> project.getTasks().stream()).anyMatch(task -> task.getUuid().equals(workDocument.getTaskId())))
                .collect(Collectors.toList());
        */

        LocalDate startDate = contract.getActiveFrom();
        LocalDate endDate = contract.getActiveTo();

        if(startDate.withDayOfMonth(1).equals(endDate.withDayOfMonth(1))) {
            // Kontrakten varer mindre end én måned, så den skal behandles særligt
        }

        if(startDate.getDayOfMonth()>1) {
            for (int i = startDate.getDayOfMonth(); i <= getLastDayOfMonth(startDate).getDayOfMonth(); i++) {

            }
            startDate = startDate.withDayOfMonth(1).plusMonths(1);
        }

        if(endDate.getDayOfMonth()<getLastDayOfMonth(endDate).getDayOfMonth()) {
            // handle early stop
            endDate = endDate.withDayOfMonth(1).minusMonths(1);
        }

        while (!startDate.isEqual(endDate)) {
            //workDocumentMap.get()
        }
    }
}
