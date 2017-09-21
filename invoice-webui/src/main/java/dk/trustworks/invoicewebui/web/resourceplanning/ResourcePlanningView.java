package dk.trustworks.invoicewebui.web.resourceplanning;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.resourceplanning.components.Card;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesHeatMap;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesView;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@SpringView(name = ResourcePlanningView.VIEW_NAME)
public class ResourcePlanningView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private SalesView salesView;

    public static final String VIEW_NAME = "availabilityplanning";
    public static final String MENU_NAME = "Availability";
    public static final String VIEW_BREADCRUMB = "Availability Planning";
    public static final FontIcon VIEW_ICON = MaterialIcons.TIMELINE;


    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(salesView.init(), VIEW_ICON, MENU_NAME, "Availability Planning", VIEW_BREADCRUMB);

        //SalesHeatMap salesHeatMap = new SalesHeatMap(localDateStart, localDateEnd);

        //salesView.addComponent(salesHeatMap.getAvailabilityChart());
        //salesView.addComponent(salesHeatMap.getBudgetGrid());
        //addComponent(salesView);
    }
/*
    @WebServlet(urlPatterns = "/*", name = "SalesPortalServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ResourcePlanningView.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);
        }

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener((SessionInitListener) sessionInitEvent -> sessionInitEvent.getSession().addBootstrapListener(new VaadinBootstrapListener()));
        }
    }*/
}
