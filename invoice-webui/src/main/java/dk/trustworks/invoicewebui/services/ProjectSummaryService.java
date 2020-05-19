package dk.trustworks.invoicewebui.services;

import com.google.common.collect.Lists;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import dk.trustworks.invoicewebui.model.enums.InvoiceType;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.network.dto.enums.ProjectSummaryType;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.repositories.ReceiptsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 06/07/2017.
 */

@Service
public class ProjectSummaryService {

    protected static Logger logger = LoggerFactory.getLogger(ProjectSummaryService.class.getName());

    private final ReceiptsRepository receiptsRepository;

    private final ContractService contractService;

    private final ProjectService projectService;

    private final WorkService workService;

    private final InvoiceRepository invoiceClient;

    @Autowired
    public ProjectSummaryService(ReceiptsRepository receiptsRepository, ContractService contractService, ProjectService projectService, WorkService workService, InvoiceRepository invoiceClient) {
        this.receiptsRepository = receiptsRepository;
        this.contractService = contractService;
        this.projectService = projectService;
        this.workService = workService;
        this.invoiceClient = invoiceClient;
    }

    @Transactional
    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(int year, int month) {
        logger.info("InvoiceController.loadProjectSummaryByYearAndMonth");
        logger.info("year = [" + year + "], month = [" + month + "]");
        logger.info("LOAD findByYearAndMonth");
        LocalDate periodFrom = LocalDate.of(year, month, 1);
        System.out.println("periodFrom = " + periodFrom);
        LocalDate periodTo = periodFrom.withDayOfMonth(periodFrom.getMonth().length(periodFrom.isLeapYear()));
        System.out.println("periodTo = " + periodTo);
        List<Work> workResources = workService.findByYearAndMonth(year, month);
        logger.info("workResources.size() = " + workResources.size());

        Collection<Invoice> invoices = invoiceClient.findByYearAndMonth(year, month-1);

        Map<String, ProjectSummary> projectSummaryMap = new HashMap<>();

        List<Receipt> receiptList = receiptsRepository.findByReceiptdateIsBetween(periodFrom, periodTo);
        logger.info("receiptList.size() = " + receiptList.size());
        for (Receipt receipt : receiptList) {
            logger.info("receipt = " + receipt);
            Project project = receipt.getProject();
            Client client = project.getClient();

            double invoicedamount = 0.0;

            if(!projectSummaryMap.containsKey(project.getUuid()+"_receipt")) {
                logger.info("new summary");
                int numberOfInvoicesRelatedToProject = 0;

                List<Invoice> relatedInvoices = new ArrayList<>();
                for (Invoice invoice : invoices) {
                    if(invoice.projectuuid.equals(project.getUuid()) &&
                            invoice.getContractuuid().equals("receipt") && (
                            invoice.status.equals(InvoiceStatus.CREATED)
                                    || invoice.status.equals(InvoiceStatus.SUBMITTED)
                                    || invoice.status.equals(InvoiceStatus.PAID)
                                    || invoice.status.equals(InvoiceStatus.CREDIT_NOTE))) {
                        numberOfInvoicesRelatedToProject++;
                        relatedInvoices.add(invoice);
                        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                            invoicedamount += (invoice.type.equals(InvoiceType.INVOICE)?
                                    (invoiceitem.hours*invoiceitem.rate):
                                    -(invoiceitem.hours*invoiceitem.rate));
                        }
                    }
                }

                List<Invoice> relatedDraftInvoices = invoices.stream().filter(invoice ->
                        invoice.projectuuid.equals(project.getUuid()) &&
                                invoice.getContractuuid().equals("receipt") && (
                                invoice.status.equals(InvoiceStatus.DRAFT))
                ).collect(Collectors.toList());


                ProjectSummary projectSummary = new ProjectSummary(
                        "receipt", project.getUuid(),
                        project.getName(),
                        client,
                        client.getName(),
                        project.getCustomerreference(),
                        0,
                        invoicedamount, numberOfInvoicesRelatedToProject, ProjectSummaryType.RECEIPT);
                projectSummary.setInvoiceList(relatedInvoices);
                projectSummary.setDraftInvoiceList(relatedDraftInvoices);
                projectSummaryMap.put(project.getUuid()+"_receipt", projectSummary);
                logger.info("Created new projectSummary: " + projectSummary);
            }

            ProjectSummary projectSummary = projectSummaryMap.get(project.getUuid()+"_receipt");
            projectSummary.addAmount(receipt.getAmount());
        }


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

                List<Invoice> relatedInvoices = new ArrayList<>();
                for (Invoice invoice : invoices) {
                    System.out.println("invoice = " + invoice);
                    if(invoice.projectuuid.equals(project.getUuid()) &&
                            invoice.getContractuuid().equals(contractuuid) && (
                            invoice.status.equals(InvoiceStatus.CREATED)
                            || invoice.status.equals(InvoiceStatus.SUBMITTED)
                            || invoice.status.equals(InvoiceStatus.PAID)
                            || invoice.status.equals(InvoiceStatus.CREDIT_NOTE))) {
                        numberOfInvoicesRelatedToProject++;
                        relatedInvoices.add(invoice);
                        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                            invoicedamount += (invoice.type.equals(InvoiceType.INVOICE)?
                                    (invoiceitem.hours*invoiceitem.rate):
                                    -(invoiceitem.hours*invoiceitem.rate));
                        }
                    }
                }

                List<Invoice> relatedDraftInvoices = invoices.stream().filter(invoice ->
                    invoice.projectuuid.equals(project.getUuid()) &&
                            invoice.getContractuuid().equals(contractuuid) && (
                            invoice.status.equals(InvoiceStatus.DRAFT))
                ).collect(Collectors.toList());

                ProjectSummary projectSummary = new ProjectSummary(
                        contractuuid, project.getUuid(),
                        project.getName(),
                        client,
                        client.getName(),
                        project.getCustomerreference(),
                        0,
                        invoicedamount, numberOfInvoicesRelatedToProject, ProjectSummaryType.CONTRACT);
                projectSummary.setInvoiceList(relatedInvoices);
                projectSummary.setDraftInvoiceList(relatedDraftInvoices);
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

    public Invoice createInvoiceFromProject(ProjectSummary projectSummary, int year, int month) {
        logger.info("InvoiceController.createInvoiceFromProject");
        logger.info("projectuuid = [" + projectSummary.getProjectuuid() + "], year = [" + year + "], month = [" + month + "]");
        logger.info("type = "+projectSummary.getProjectSummaryType().name());

        Invoice invoice = null;

        if(projectSummary.getProjectSummaryType().equals(ProjectSummaryType.RECEIPT)) {
            LocalDate periodFrom = LocalDate.of(year, month + 1, 1);
            LocalDate periodTo = periodFrom.withDayOfMonth(periodFrom.lengthOfMonth());

            Project project = projectService.findOne(projectSummary.getProjectuuid());

            List < Receipt > receiptList = receiptsRepository.findByProjectuuidAndReceiptdateIsBetween(project.getUuid(), periodFrom, periodTo);
            logger.info("receiptList.size() = " + receiptList.size());

            for (Receipt receipt : receiptList) {
                if(invoice == null) {
                    invoice = new Invoice(InvoiceType.INVOICE,
                            "receipt",
                            project.getUuid(),
                            project.getName(),
                            0.0,
                            year,
                            month,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            LocalDate.now().withYear(year).withMonth(month+1).withDayOfMonth(LocalDate.now().withYear(year).withMonth(month+1).lengthOfMonth()),
                            project.getCustomerreference(),
                            "",
                            "");
                    logger.info("Created new invoice: "+invoice);
                }

                InvoiceItem invoiceItem = new InvoiceItem(receipt.getDescription(), "udlæg d. "+receipt.getReceiptdate().format(DateTimeFormatter.ofPattern("d. MMM yyyy")), receipt.getAmount(), 1.0);
                invoiceItem.uuid = UUID.randomUUID().toString();
                invoice.invoiceitems.add(invoiceItem);
                logger.info("Created new invoice item: "+invoiceItem);
            }
        } else if (projectSummary.getProjectSummaryType().equals(ProjectSummaryType.CONTRACT)) {
            System.out.println("beginning...");
            //List<Work> workResources = workClient.findByYearAndMonth(year, month);
            List<Work> workResources = workService.findByYearAndMonthAndProject(year, month+1, projectSummary.getProjectuuid());
            System.out.println("workResources.size() = " + workResources.size());
            Map<String, InvoiceItem> invoiceItemMap = new HashMap<>();

            Contract contract = contractService.findOne(projectSummary.getContractuuid());
            System.out.println("contract = " + contract);

            for (Work workResource : workResources) {
                if (workResource.getWorkduration() == 0) continue;
                Task task = workResource.getTask();
                logger.info("task = " + task);

                Project project = task.getProject();
                //if(!project.getUuid().equals(projectSummary.getProjectuuid())) continue;
                logger.info("project = " + project);

                if (!contractService.findContractByWork(workResource, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED).getUuid().equals(contract.getUuid()))
                    continue;

                User user = workResource.getUser();
                System.out.println("user = " + user);

                if (contract.getClientdata() == null) logger.info("clientdata null: " + contract);
                Clientdata clientdata = (contract.getClientdata() != null) ? contract.getClientdata() : new Clientdata();

                if (invoice == null) {
                    System.out.println("new invoice");
                    invoice = new Invoice(InvoiceType.INVOICE,
                            contract.getUuid(),
                            project.getUuid(),
                            project.getName(),
                            0.0,
                            year,
                            month,
                            clientdata.getClientname(),
                            clientdata.getStreetnamenumber(),
                            clientdata.getOtheraddressinfo(),
                            clientdata.getPostalcode() + " " + clientdata.getCity(),
                            clientdata.getEan(),
                            clientdata.getCvr(),
                            clientdata.getContactperson(),
                            LocalDate.now().withYear(year).withMonth(month + 1).withDayOfMonth(LocalDate.now().withYear(year).withMonth(month + 1).lengthOfMonth()),
                            project.getCustomerreference(),
                            contract.getRefid(),
                            "");
                    logger.info("Created new invoice: " + invoice);
                }

                Double rate = contractService.findConsultantRateByWork(workResource, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
                System.out.println("rate = " + rate);

                if (rate == null) {
                    logger.error("Taskworkerconstraint could not be found for user (link: " + user.getUuid() + ") and task (link: " + task.getUuid() + ")");
                    invoice.errors = true;
                    Notification.show("User not assigned",
                            user.getUsername() + " is not assigned to task \"" + task.getName() + "\" " +
                                    "on the project \"" + project.getName() + "\". " +
                                    "Please fix this on project page.",
                            Notification.Type.ERROR_MESSAGE);
                    return null;
                }
                if (!invoiceItemMap.containsKey(contract.getUuid() + project.getUuid() + workResource.getUser().getUuid() + workResource.getTask().getUuid())) {
                    InvoiceItem invoiceItem = new InvoiceItem(user.getFirstname() + " " + user.getLastname(),
                            task.getName(),
                            rate,
                            0.0);
                    invoiceItem.uuid = UUID.randomUUID().toString();
                    invoiceItemMap.put(contract.getUuid() + project.getUuid() + workResource.getUser().getUuid() + workResource.getTask().getUuid(), invoiceItem);
                    invoice.invoiceitems.add(invoiceItem);
                    logger.info("Created new invoice item: " + invoiceItem);
                }
                System.out.println("...end");
                invoiceItemMap.get(contract.getUuid() + project.getUuid() + workResource.getUser().getUuid() + workResource.getTask().getUuid()).hours += workResource.getWorkduration();
            }
        }
        System.out.println("invoice = " + invoice);
        return invoiceClient.save(invoice);
    }
}
