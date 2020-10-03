package dk.trustworks.invoicewebui.web.time;

/**
 * Created by hans on 16/08/2017.
 */
/*
@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = TimeManagerView.VIEW_NAME)
public class TimeManagerView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(TimeManagerView.class.getName());

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TimeManagerImpl timeManager;

    public static final String VIEW_NAME = "timeregistration";
    public static final String MENU_NAME = "Time Sheet";
    public static final String VIEW_BREADCRUMB = "TimeManager / Time Sheet";
    public static final FontIcon VIEW_ICON = MaterialIcons.ACCESS_TIME;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        this.setResponsive(true);
        mainTemplate.setMainContent(timeManager.init(), VIEW_ICON, MENU_NAME, "You are probably doing this late...", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        authorizer.authorize(this, RoleType.USER);
    }
}

 */
