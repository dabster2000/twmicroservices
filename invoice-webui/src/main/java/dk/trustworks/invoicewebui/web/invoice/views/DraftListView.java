package dk.trustworks.invoicewebui.web.invoice.views;

/**
 * Created by hans on 11/07/2017.
 */
/*
@Push
@AccessRules(roleTypes = {RoleType.ACCOUNTING})
@SpringView(name = DraftListView.VIEW_NAME)
public class DraftListView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "invoice_drafts";
    public static final String MENU_NAME = "Drafts";
    public static final String VIEW_BREADCRUMB = "Invoice / Drafts";
    public static final FontIcon VIEW_ICON = MaterialIcons.RECEIPT;

    protected static Logger logger = LoggerFactory.getLogger(DraftListView.class.getName());

    @Autowired
    private DraftListImpl draftListComponent;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(draftListComponent, VIEW_ICON, MENU_NAME, "A list of money germs!", VIEW_BREADCRUMB);
    }

    public void receiveBroadcast(String message) {
        logger.info("MainWindowImpl.receiveBroadcast");
        logger.info("message = " + message);
/*
        int numberOfDrafts = invoiceRepository.findByStatus(DRAFT).size();
        if(this.getUI() != null) this.getUI().access(() -> {
            draftListComponent.loadInvoicesToGrid();
            if(numberOfDrafts>0) leftMenu.getMenuItems().get(VIEW_NAME).getMenuItemColumn().getComponent().setCaption(MENU_NAME+" ("+numberOfDrafts+")");
            else leftMenu.getMenuItems().get(VIEW_NAME).getMenuItemColumn().getComponent().setCaption(MENU_NAME);
        });
*/
   // }
/*
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
        draftListComponent.loadInvoicesToGrid();
        //leftMenu.getMenuItems().get(VIEW_NAME).getParent().foldOut();
    }


}


 */