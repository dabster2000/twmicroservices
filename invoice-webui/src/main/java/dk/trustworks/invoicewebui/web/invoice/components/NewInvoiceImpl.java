package dk.trustworks.invoicewebui.web.invoice.components;

/**
 * Created by hans on 12/07/2017.
 */
/*
@Push
@SpringComponent
@UIScope
public class NewInvoiceImpl extends NewInvoiceDesign {

    protected static Logger logger = LoggerFactory.getLogger(NewInvoiceImpl.class.getName());

    private final ProjectSummaryService projectSummaryClient;

    private final InvoiceService invoiceService;

    private final PhotoService photoService;

    private final ProjectService projectService;

    private VerticalLayout errorList;

    private Card errorCard;

    @Autowired
    public NewInvoiceImpl(ProjectSummaryService projectSummaryClient, InvoiceService invoiceService, PhotoService photoService, ProjectService projectService) {
        this.projectSummaryClient = projectSummaryClient;
        this.invoiceService = invoiceService;
        this.photoService = photoService;
        this.projectService = projectService;
    }

    @PostConstruct
    public void postConstruct() {
        btnCreateInvoice.addClickListener(event -> {
            logger.info(gridProjectSummaryList.getSelectedItems().size()+" new invoice created");
            for (ProjectSummary projectSummary : gridProjectSummaryList.getSelectedItems()) {
                projectSummaryClient.createInvoiceFromProject(projectSummary, cbSelectYearMonth.getValue().getDate().getYear(), cbSelectYearMonth.getValue().getDate().getMonthValue()-1);
                reloadData();
            }
            Notification.show("Invoice created",
                    "One or more invoices have been created. You can edit in them in the Drafts section",
                    Notification.Type.TRAY_NOTIFICATION);
        });

        if(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser().getUuid().equals("7948c5e8-162c-4053-b905-0f59a21d7746")) btnCreateBlankInvoice.setVisible(true);
        btnCreateBlankInvoice.addClickListener(event -> {
            logger.info("New blank invoice created");
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            invoiceService.createBlankInvoice(year, month);
            Notification.show("Invoice created",
                    "A new blank invoice has been created. You can edit in the Drafts section",
                    Notification.Type.TRAY_NOTIFICATION);
        });

        errorCard = new Card();
        errorCard.getContent().setHeight(250, Unit.PIXELS);
        errorCard.getContent().addStyleName("v-scrollable");
        errorCard.getLblTitle().setValue("Invoice errors");
    }

    private void createErrorList(Card errorCard) {
        errorCard.getContent().removeAllComponents();
        errorList = new VerticalLayout();
        errorList.addComponent(new MLabel("The invoices for this months contain the following errors:").withStyleName("failure"));
        errorCard.getContent().addComponent(errorList);
        vlErrorCardContainer.addComponent(errorCard);
    }

    public NewInvoiceImpl init() {
        logger.info("NewInvoiceImpl.init");

        List<YearMonthSelect> yearMonthList = createYearMonthSelector();
        cbSelectYearMonth.setItems(yearMonthList);
        cbSelectYearMonth.setItemCaptionGenerator(c -> c.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        cbSelectYearMonth.setEmptySelectionAllowed(false);
        cbSelectYearMonth.setSelectedItem(yearMonthList.get(1));
        cbSelectYearMonth.addValueChangeListener(event -> reloadData());

        Column colRegisteredAmount = gridProjectSummaryList.getColumn("registeredamount");
        colRegisteredAmount.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        colRegisteredAmount.setStyleGenerator(item -> "v-align-right");

        Column colInvoicedAmount = gridProjectSummaryList.getColumn("invoicedamount");
        colInvoicedAmount.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        colInvoicedAmount.setStyleGenerator(item -> "v-align-right");

        gridProjectSummaryList.setSelectionMode(Grid.SelectionMode.MULTI);
        gridProjectSummaryList.addSelectionListener(event -> {
            logger.debug("event = " + event);
            Set<ProjectSummary> selectedItems = event.getAllSelectedItems();
            switch (selectedItems.size()) {
                case 0:
                    btnCreateInvoice.setCaption("Create invoice");
                    btnCreateInvoice.setEnabled(false);
                    break;
                case 1:
                    btnCreateInvoice.setCaption("Create invoice");
                    btnCreateInvoice.setEnabled(true);
                    break;
                default:
                    btnCreateInvoice.setCaption("Create invoices");
                    btnCreateInvoice.setEnabled(true);
            }
        });

        gridProjectSummaryList.setSizeFull();

        addTour();

        reloadData();
        return this;
    }

    public void reloadData() {
        logger.info("NewInvoiceImpl.reloadData");
        long start = System.currentTimeMillis();
        logger.debug("start = " + start);
        createErrorList(errorCard);
        List<ProjectSummary> projectSummaries = projectSummaryClient.loadProjectSummaryByYearAndMonth(cbSelectYearMonth.getValue().getDate().getYear(), cbSelectYearMonth.getValue().getDate().getMonthValue());

        gridProjectSummaryList.setDataProvider(DataProvider.ofCollection(projectSummaries));
        gridProjectSummaryList.getDataProvider().refreshAll();
        for (ProjectSummary projectSummary : projectSummaries) {
            if(projectSummary.getErrors().size()>0) {
                vlErrorCardContainer.setVisible(true);
                for (String error : projectSummary.getErrors()) {
                    errorList.addComponent(new MLabel(error).withWidth(100, Unit.PERCENTAGE));
                }
            }
        }

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        this.cardContainer.removeAllComponents();
        this.cardContainer.addComponent(responsiveLayout);

        ResponsiveRow responsiveRow = responsiveLayout.addRow();
        for (ProjectSummary projectSummary : projectSummaries) {
            InvoiceCandidateDesign invoiceCandidateDesign = new InvoiceCandidateDesign();
            invoiceCandidateDesign.getLblTotalInvoiced().setValue(NumberConverter.formatCurrency(projectSummary.getRegisteredamount()));
            invoiceCandidateDesign.getLblTotalNotInvoiced().setValue(NumberConverter.formatCurrency(projectSummary.getRegisteredamount()-projectSummary.getInvoicedamount()));
            invoiceCandidateDesign.getLblSOHours().setValue("15");
            invoiceCandidateDesign.getImgTop().setSource(photoService.getRelatedPhoto(projectService.findOne(projectSummary.getProjectuuid()).getClient().getUuid()));

            GridLayout gridInvoices = invoiceCandidateDesign.getGridInvoices();
            gridInvoices.setRows(1+projectSummary.getInvoiceList().size());
            for (Invoice invoice : projectSummary.getInvoiceList()) {
                gridInvoices.addComponent(new MLabel(invoice.getInvoicedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                gridInvoices.addComponent(new MLabel(invoice.getType().name()));
                gridInvoices.addComponent(new MLabel(NumberConverter.formatCurrency(invoice.getSumWithTax())));
            }
            responsiveRow.addColumn().withDisplayRules(12, 12, 4, 3).withComponent(invoiceCandidateDesign);
        }


        double end = System.currentTimeMillis() - start;
        logger.debug("end = " + end);
    }

    private List<YearMonthSelect> createYearMonthSelector() {
        List<YearMonthSelect> yearMonthSelectList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 2, 1);
        LocalDate countDate = LocalDate.now();
        while (countDate.isAfter(startDate)) {
            yearMonthSelectList.add(new YearMonthSelect(countDate));
            countDate = countDate.minusMonths(1);
        }

        return yearMonthSelectList;
    }

    private void addTour() {
        btnTour.addClickListener(e -> {
            Tour tour = new Tour();
            tour.addStep(getStep1(cbSelectYearMonth));
            tour.addStep(getStep2(cbSelectYearMonth));
            tour.addStep(getStep3(gridProjectSummaryList));
            tour.addStep(getStep4(btnCreateInvoice));
            tour.start();
        });
    }

    private Step getStep1(AbstractComponent attachTo) {
        return new StepBuilder()
                //.withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(430, Unit.PIXELS)
                .withTitle("Welcome to InvoiceManager!")
                .withText(
                        "This page is a brief overview of the projects that have been worked on in the chosen month. This is meant as " +
                                "the entry point where new invoices are made. When the page is loaded for the first time, the previous month is selected by default." +
                                "<br />" +
                                "<br />" +
                                "This is the first step in an invoice life cycle. The go from: " +
                                "<br />" +
                                "DRAFTS -> CREATED -> SENT -> PAID" +
                                "<br />" +
                                "<br />" +
                                "Some end the lifecycle as PAID, but if something wrong happened it may be necessary to send an credit note. In that case the invoice " +
                                "gets a new final state: CREDIT NOTE" +
                                "<br />" +
                                "More on that topic later.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .build();
    }

    private Step getStep2(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(200, Unit.PIXELS)
                .withTitle("Selecting year and month")
                .withText("You may select another year/month from this dropdown menu.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep3(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(400, Unit.PIXELS)
                .withTitle("Project list")
                .withText("This list contains all the projects that have been worked on during the chosen month. " +
                        "It only contains the most useful information about the project in order for you to navigate through them. " +
                        "If you need to change any of the shown data, you need to do it from the TimeManager. " +
                        "<br />" +
                        "<br />" +
                        "The grid shows details such as the clients name, project name, a brief description, and how many hours have been registered " +
                        "on the project. It also shows how many invoices have been created based on this project on the chosen month. This also includes " +
                        "Credit Notes. This way you are less likely to create more than one invoice per project.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep4(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(300, Unit.PIXELS)
                .withTitle("Creating invoice drafts")
                .withText("From the grid you may choose one or more projects - fx the projects that you are managing. " +
                        "When you have selected the relevant projects, press this button - create invoices. " +
                        "<br />" +
                        "<br />" +
                        "The InvoiceManager will then create invoice drafts for all the selected projects for the chosen month. " +
                        "This is the state of an invoice life.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Finished", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }
}

 */
