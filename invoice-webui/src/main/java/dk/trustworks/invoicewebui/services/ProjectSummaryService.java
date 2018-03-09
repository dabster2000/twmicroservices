package dk.trustworks.invoicewebui.services;

import com.google.common.collect.Lists;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.repositories.TaskworkerconstraintRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by hans on 06/07/2017.
 */

@Service
public class ProjectSummaryService {

    protected static Logger logger = LoggerFactory.getLogger(ProjectSummaryService.class.getName());

    @Autowired
    private TaskworkerconstraintRepository taskworkerconstraintClient;

    @Autowired
    private WorkRepository workClient;

    @Autowired
    private InvoiceRepository invoiceClient;

    @Transactional
    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(int year, int month) {
        logger.info("InvoiceController.loadProjectSummaryByYearAndMonth");
        logger.info("year = [" + year + "], month = [" + month + "]");
        List<Work> workResources = workClient.findByYearAndMonth(year, month);
        logger.info("workResources.getContent().size() = " + workResources.size());

        Collection<Invoice> invoices = invoiceClient.findByYearAndMonth(year, month);

        Map<String, ProjectSummary> projectSummaryMap = new HashMap<>();
/*
        Map<String, Task> taskMap = new HashMap<>();
        for (Task taskResource : taskClient.findAll()) {
            taskMap.put(taskResource.getUuid(), taskResource);
        }

        Map<String, Project> projectMap = new HashMap<>();
        for (Project projectResource : projectClient.findAll()) {
            projectMap.put(projectResource.getUuid(), projectResource);
        }

        Map<String, Client> clientMap = new HashMap<>();
        for (Client clientResource : clientClient.findAll()) {
            clientMap.put(clientResource.getUuid(), clientResource);
        }

        Map<String, Taskworkerconstraint> taskworkerconstraintMap = new HashMap<>();
        for (Taskworkerconstraint taskworkerconstraintResource : taskworkerconstraintClient.findAll()) {
            taskworkerconstraintMap.put(
                    taskworkerconstraintResource.getUser().getUuid()+"_"
                    +taskworkerconstraintResource.getTask().getUuid(),
                    taskworkerconstraintResource
            );
        }
*/

        for (Work work : workResources) {
            Task task = work.getTask();
            Project project = task.getProject();
            Client client = project.getClient();

            double invoicedamount = 0.0;

            if(!projectSummaryMap.containsKey(project.getUuid())) {
                int numberOfInvoicesRelatedToProject = 0;
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
                ProjectSummary projectSummary = new ProjectSummary(
                        project.getUuid(),
                        project.getName(),
                        client.getName(),
                        project.getCustomerreference(),
                        0,
                        invoicedamount, numberOfInvoicesRelatedToProject);
                projectSummaryMap.put(project.getUuid(), projectSummary);
                logger.info("Created new projectSummary: " + projectSummary);
            }
            if(work.getUser()==null) logger.info("work u = " + work);
            if(work.getTask()==null) logger.info("work t = " + work);
            List<Taskworkerconstraint> taskworkerconstraintList = taskworkerconstraintClient.findByTaskAndUser(work.getTask(), work.getUser());
            if(taskworkerconstraintList.size()==0) continue;
            Taskworkerconstraint taskworkerconstraint = taskworkerconstraintList.get(0);
            projectSummaryMap.get(project.getUuid()).addAmount(work.getWorkduration() * (taskworkerconstraint.getPrice()));
        }
        return Lists.newArrayList(projectSummaryMap.values());
    }

    public void createInvoiceFromProject(String projectuuid, int year, int month) {
        logger.info("InvoiceController.createInvoiceFromProject");
        logger.info("projectuuid = [" + projectuuid + "], year = [" + year + "], month = [" + month + "]");
        List<Work> workResources = workClient.findByYearAndMonth(year, month);
        Invoice invoice = null;
        Map<String, InvoiceItem> invoiceItemMap = new HashMap<>();

        for (Work workResource : workResources) {
            if(workResource.getWorkduration() == 0) continue;
            Task task = workResource.getTask();
            logger.info("task = " + task);

            Project project = task.getProject();
            if(!project.getUuid().equals(projectuuid)) continue;
            logger.info("project = " + project);

            //Resource<User> userResource = userClient.findUserByRestLink(userLink.getHref());
            User user = workResource.getUser();
            //logger.info("user = " + user);

            //Clientdata clientdata = (clientdataResource!=null)?clientdataResource.getContent():new Clientdata();
            if(project.getClientdata()==null) logger.info("clientdata null: "+project);
            Clientdata clientdata = (project.getClientdata()!=null)?project.getClientdata():new Clientdata();

            //Client client = project.getClient();

            List<Taskworkerconstraint> taskworkerconstraintResources = task.getTaskworkerconstraint();

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
                        "");
                invoice.uuid = UUID.randomUUID().toString();
                logger.info("Created new invoice: "+invoice);
            }
            Taskworkerconstraint taskworkerconstraint = null;
            for (Taskworkerconstraint taskworkerconstraintResource : taskworkerconstraintResources) {
                if(taskworkerconstraintResource.getTask().getUuid().equals(task.getUuid()) && taskworkerconstraintResource.getUser().getUuid().equals(user.getUuid())) {
                    taskworkerconstraint = taskworkerconstraintResource;
                }
            }
            if(taskworkerconstraint == null) {
                logger.error("Taskworkerconstraint could not be found for user (link: "+user.getUuid()+") and task (link: "+task.getUuid()+")");
                invoice.errors = true;
                Notification.show("User not assigned",
                        user.getUsername()+ " is not assigned to task \""+ task.getName()+"\" " +
                                "on the project \""+project.getName()+"\". " +
                                "Please fix this on project page.",
                        Notification.Type.ERROR_MESSAGE);
                return;
            }
            if(!invoiceItemMap.containsKey(project.getUuid()+taskworkerconstraint.getUuid())) {
                InvoiceItem invoiceItem = new InvoiceItem(user.getFirstname() + " " + user.getLastname(),
                        task.getName(),
                        taskworkerconstraint.getPrice(),
                        0.0);
                invoiceItem.uuid = UUID.randomUUID().toString();
                invoiceItemMap.put(project.getUuid()+taskworkerconstraint.getUuid(), invoiceItem);
                invoice.invoiceitems.add(invoiceItem);
                logger.info("Created new invoice item: "+invoiceItem);
            }
            invoiceItemMap.get(project.getUuid()+taskworkerconstraint.getUuid()).hours += workResource.getWorkduration();
        }

        invoiceClient.save(invoice);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveInvoice(@RequestBody Invoice invoice) {
        logger.debug("InvoiceController.saveInvoice");
        logger.debug("invoice = [" + invoice + "]");
        invoiceClient.save(invoice);
    }
}


/*
logger.info("InvoiceController.loadProjectSummaryByYearAndMonth");
        logger.info("year = [" + year + "], month = [" + month + "]");
        Resources<Resource<Work>> workResources = workClient.findByYearAndMonth(year, month);
        logger.info("workResources.getContent().size() = " + workResources.getContent().size());

        Map<String, Invoice> invoiceMap = new HashMap<>();
        Map<String, InvoiceItem> invoiceItemMap = new HashMap<>();

        for (Resource<Work> workResource : workResources) {
            Link taskLink = workResource.getLink("task");
            Link userLink = workResource.getLink("user");
            Resource<User> userResource = userClient.findUserByRestLink(userLink.getHref());
            Resource<Task> taskResource = taskClient.findTaskByRestLink(taskLink.getHref());
            Resources<Resource<Taskworkerconstraint>> taskworkerconstraintResources = taskworkerconstraintClient.findTaskworkerconstraintsByRestLink(taskResource.getLink("taskworkerconstraint").getHref());
            Resource<Project> projectResource = projectClient.findProjectByRestLink(taskResource.getLink("project").getHref());
            Resource<Clientdata> clientdataResource = clientdataClient.findClientdataByRestLink(projectResource.getLink("clientData").getHref());
            Resource<Client> clientResource = clientClient.findClientByRestLink(projectResource.getLink("client").getHref());

            User user = userResource.getContent();
            Task task = taskResource.getContent();
            Project project = projectResource.getContent();
            Clientdata clientdata = (clientdataResource!=null)?clientdataResource.getContent():new Clientdata();
            Client client = clientResource.getContent();

            if(!invoiceMap.containsKey(project.getUuid())) {
                Invoice invoice = new Invoice(InvoiceType.INVOICE,
                        project.getUuid(),
                        project.getName(),
                        year,
                        month,
                        client.getName(),
                        clientdata.getStreetnamenumber(),
                        clientdata.getPostalcode()+" "+ clientdata.getCity(),
                        clientdata.getEan(),
                        clientdata.getCvr(),
                        clientdata.getContactperson(),
                        LocalDate.now(),
                        project.getCustomerreference());
                invoiceMap.put(project.getUuid(), invoice);
                logger.info("Created new invoice: "+invoice);
            }
            Taskworkerconstraint taskworkerconstraint = null;
            for (Resource<Taskworkerconstraint> taskworkerconstraintResource : taskworkerconstraintResources) {
                if(taskworkerconstraintResource.getContent().getTaskuuid().equals(task.getUuid()) && taskworkerconstraintResource.getContent().getUseruuid().equals(user.getUuid())) {
                    taskworkerconstraint = taskworkerconstraintResource.getContent();
                }
            }
            if(taskworkerconstraint == null) {
                logger.error("Taskworkerconstraint could not be found for user (link: "+user.getUuid()+") and task (link: "+task.getUuid()+")");
                invoiceMap.get(project.getUuid()).errors = true;
                continue;
            }
            if(!invoiceItemMap.containsKey(project.getUuid()+taskworkerconstraint.getUuid())) {
                InvoiceItem invoiceItem = new InvoiceItem(task.getName(),
                        "",
                        user.getFirstname() + " " + user.getLastname(),
                        taskworkerconstraint.getPrice(),
                        0.0);

                invoiceItemMap.put(project.getUuid()+taskworkerconstraint.getUuid(), invoiceItem);
                invoiceMap.get(project.getUuid()).invoiceitems.add(invoiceItem);
                logger.info("Created new invoice item: "+invoiceItem);
            }
            invoiceItemMap.get(project.getUuid()+taskworkerconstraint.getUuid()).hours += workResource.getContent().getWorkduration();
        }
        return invoiceMap.values();
 */