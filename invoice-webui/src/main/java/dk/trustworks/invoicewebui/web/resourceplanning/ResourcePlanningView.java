package dk.trustworks.invoicewebui.web.resourceplanning;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.View;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesHeatMap;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesView;
import org.joda.time.LocalDate;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by hans on 19/12/2016.
 */

@SpringView(name = ResourcePlanningView.VIEW_NAME)
public class ResourcePlanningView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "resourceplanning";

    @PostConstruct
    void init() {
        LocalDate localDateStart = LocalDate.now().plusMonths(0).withDayOfMonth(1);
        LocalDate localDateEnd = localDateStart.plusYears(1);

        SalesView salesView = new SalesView();
        SalesHeatMap salesHeatMap = new SalesHeatMap(localDateStart, localDateEnd);
        salesView.addComponent(salesHeatMap.getChart());
        salesView.addComponent(salesHeatMap.getAvailabilityChart());
        salesView.addComponent(salesHeatMap.getBudgetGrid());
        addComponent(salesView);
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
