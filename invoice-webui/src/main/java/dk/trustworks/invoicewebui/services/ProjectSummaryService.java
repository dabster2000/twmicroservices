package dk.trustworks.invoicewebui.services;

public class ProjectSummaryService {

/*
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
        return invoiceService.save(invoice);
    }

 */
}
