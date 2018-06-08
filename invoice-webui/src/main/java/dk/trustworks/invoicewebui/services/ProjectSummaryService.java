package dk.trustworks.invoicewebui.services;

import com.google.common.collect.Lists;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by hans on 06/07/2017.
 */

@Service
public class ProjectSummaryService {

    protected static Logger logger = LoggerFactory.getLogger(ProjectSummaryService.class.getName());


    private final ContractService contractService;

    private final WorkRepository workClient;

    private final InvoiceRepository invoiceClient;

    @Autowired
    public ProjectSummaryService(ContractService contractService, WorkRepository workClient, InvoiceRepository invoiceClient) {
        this.contractService = contractService;
        this.workClient = workClient;
        this.invoiceClient = invoiceClient;
    }

    @Transactional
    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(int year, int month) {
        logger.info("InvoiceController.loadProjectSummaryByYearAndMonth");
        logger.info("year = [" + year + "], month = [" + month + "]");
        logger.info("LOAD findByYearAndMonth");
        List<Work> workResources = workClient.findByYearAndMonth(year, month);
        logger.info("workResources.size() = " + workResources.size());

        //Collection<Invoice> invoices = invoiceClient.findByYearAndMonth(year, month);

        Map<String, ProjectSummary> projectSummaryMap = new HashMap<>();

        for (Work work : workResources) {
            if(!(work.getWorkduration()>0)) continue;
            Task task = work.getTask();
            Project project = task.getProject();
            Client client = project.getClient();
            Contract contract = contractService.findContractByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            String contractuuid = (contract==null)?"":contract.getUuid();

            double invoicedamount = 0.0;

            if(!projectSummaryMap.containsKey(contractuuid+project.getUuid())) {
                int numberOfInvoicesRelatedToProject = 0;
                /*
                for (Invoice invoice : invoices) {
                    if(invoice.projectuuid.equals(project.getUuid()) && (
                            invoice.status.equals(InvoiceStatus.CREATED)
                            || invoice.status.equals(InvoiceStatus.SUBMITTED)
                            || invoice.status.equals(InvoiceStatus.PAID)
                            || invoice.status.equals(InvoiceStatus.CREDIT_NOTE))) {
                        numberOfInvoicesRelatedToProject++;
                        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                            invoicedamount += (invoice.type.equals(InvoiceType.INVOICE)?
                                    (invoiceitem.hours*invoiceitem.rate):
                                    -(invoiceitem.hours*invoiceitem.rate));
                        }
                    }
                }
                */
                ProjectSummary projectSummary = new ProjectSummary(
                        contractuuid, project.getUuid(),
                        project.getName(),
                        client.getName(),
                        project.getCustomerreference(),
                        0,
                        invoicedamount, numberOfInvoicesRelatedToProject);
                projectSummaryMap.put(contractuuid+project.getUuid(), projectSummary);
                logger.info("Created new projectSummary: " + projectSummary);
            }
            if(work.getUser()==null) logger.info("work u = " + work);
            if(work.getTask()==null) logger.info("work t = " + work);

            ProjectSummary projectSummary = projectSummaryMap.get(contractuuid+project.getUuid());
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null) {
                projectSummary.addAmount(work.getWorkduration() * (rate));
            } else {
                projectSummary.addError(work.getUser().getUuid()+work.getTask().getProject().getUuid(),
                        "There is no valid contract for " + work.getUser().getUsername() +
                        " work on " + work.getTask().getProject().getClient().getName() +"'s project " +
                        work.getTask().getProject().getName());
            }
        }
        return Lists.newArrayList(projectSummaryMap.values());
    }

    public void createInvoiceFromProject(ProjectSummary projectSummary, int year, int month) {
        logger.info("InvoiceController.createInvoiceFromProject");
        logger.info("projectuuid = [" + projectSummary.getProjectuuid() + "], year = [" + year + "], month = [" + month + "]");
        //List<Work> workResources = workClient.findByYearAndMonth(year, month);
        List<Work> workResources = workClient.findByYearAndMonthAndProject(year, month, projectSummary.getProjectuuid());
        Invoice invoice = null;
        Map<String, InvoiceItem> invoiceItemMap = new HashMap<>();

        Contract contract = contractService.findOne(projectSummary.getContractuuid());

        for (Work workResource : workResources) {
            if(workResource.getWorkduration() == 0) continue;
            Task task = workResource.getTask();
            logger.info("task = " + task);

            Project project = task.getProject();
            //if(!project.getUuid().equals(projectSummary.getProjectuuid())) continue;
            logger.info("project = " + project);

            if(!contractService.findContractByWork(workResource, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED).getUuid().equals(contract.getUuid())) continue;

            User user = workResource.getUser();

            if(contract.getClientdata()==null) logger.info("clientdata null: "+contract);
            Clientdata clientdata = (contract.getClientdata()!=null)?contract.getClientdata():new Clientdata();

            if(invoice == null) {
                invoice = new Invoice(InvoiceType.INVOICE,
                        project.getUuid(),
                        project.getName(),
                        year,
                        month,
                        clientdata.getClientname(),
                        clientdata.getStreetnamenumber(),
                        clientdata.getOtheraddressinfo(),
                        clientdata.getPostalcode()+" "+ clientdata.getCity(),
                        clientdata.getEan(),
                        clientdata.getCvr(),
                        clientdata.getContactperson(),
                        LocalDate.now().withYear(year).withMonth(month+1).withDayOfMonth(LocalDate.now().withYear(year).withMonth(month+1).lengthOfMonth()),
                        project.getCustomerreference(),
                        contract.getRefid(),
                        "");
                logger.info("Created new invoice: "+invoice);
            }

            Double rate = contractService.findConsultantRateByWork(workResource, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);

            if(rate == null) {
                logger.error("Taskworkerconstraint could not be found for user (link: "+user.getUuid()+") and task (link: "+task.getUuid()+")");
                invoice.errors = true;
                Notification.show("User not assigned",
                        user.getUsername()+ " is not assigned to task \""+ task.getName()+"\" " +
                                "on the project \""+project.getName()+"\". " +
                                "Please fix this on project page.",
                        Notification.Type.ERROR_MESSAGE);
                return;
            }
            if(!invoiceItemMap.containsKey(contract.getUuid()+project.getUuid()+workResource.getUser().getUuid()+workResource.getTask().getUuid())) {
                InvoiceItem invoiceItem = new InvoiceItem(user.getFirstname() + " " + user.getLastname(),
                        task.getName(),
                        rate,
                        0.0);
                invoiceItem.uuid = UUID.randomUUID().toString();
                invoiceItemMap.put(contract.getUuid()+project.getUuid()+workResource.getUser().getUuid()+workResource.getTask().getUuid(), invoiceItem);
                invoice.invoiceitems.add(invoiceItem);
                logger.info("Created new invoice item: "+invoiceItem);
            }
            invoiceItemMap.get(contract.getUuid()+project.getUuid()+workResource.getUser().getUuid()+workResource.getTask().getUuid()).hours += workResource.getWorkduration();
        }

        invoiceClient.save(invoice);
    }
}
