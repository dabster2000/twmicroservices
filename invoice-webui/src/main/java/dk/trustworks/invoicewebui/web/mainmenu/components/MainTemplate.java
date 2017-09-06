package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.services.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 28/08/2017.
 */

@SpringComponent
@SpringUI
public class MainTemplate extends VerticalLayout {

    protected static Logger logger = LoggerFactory.getLogger(MainTemplate.class.getName());

    private final ResponsiveColumn mainSectionCol;

    @Autowired
    public MainTemplate(LeftMenu leftMenu) {
        setSizeFull(); // set the size of the UI to fill the screen
        setMargin(false);
        setSpacing(true);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        addComponent(responsiveLayout);

        // ResponsiveLayouts have rows
        // Our first row will contain our 2 Columns
        // The Menu Column & the Main Column
        ResponsiveRow rootRow = responsiveLayout.addRow();
        rootRow.setHeight("100%");

        ResponsiveColumn sideMenuCol = new ResponsiveColumn(12, 12, 2, 2);
        sideMenuCol.addStyleName("dark-blue");
        rootRow.addColumn(sideMenuCol);

        // Fluent API

        mainSectionCol = rootRow.addColumn().withDisplayRules(12,12,10,10);
        mainSectionCol.addStyleName("bg-grey");

        sideMenuCol.setComponent(leftMenu);
    }

    public void setMainContent(Component component) {
        logger.debug("MainTemplate.setMainContent");
        logger.debug("component = [" + component + "]");
        logger.debug("mainSectionCol = " + mainSectionCol);
        mainSectionCol.setComponent(component);
    }
}
