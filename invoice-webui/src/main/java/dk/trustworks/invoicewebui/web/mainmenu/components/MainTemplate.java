package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.web.views.TrustworksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

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
        setSpacing(false);

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

        VerticalLayout vlContainer = new VerticalLayout();
        vlContainer.setMargin(false);
        vlContainer.setSpacing(false);
        vlContainer.setSizeFull();
        mainSectionCol.setComponent(vlContainer);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setWidth("100%");
        responsiveLayout.setHeight("75px");
        responsiveLayout.addStyleName("white-bg");
        vlContainer.addComponent(responsiveLayout);

        Label spacer = new Label("");
        spacer.setWidth("30px");
        Label icon = new Label(MaterialIcons.DASHBOARD.getHtml(), ContentMode.HTML);
        icon.addStyleName("huge");
        Label title = new Label("Title");
        title.addStyleName("large");
        Label subTitle = new Label("World of Trustworks");
        subTitle.addStyleName("tiny");
        VerticalLayout vlTitle = new VerticalLayout(title, subTitle);
        vlTitle.setMargin(false);
        vlTitle.setSpacing(false);
        vlTitle.setComponentAlignment(title, Alignment.BOTTOM_LEFT);
        vlTitle.setComponentAlignment(subTitle, Alignment.TOP_LEFT);
        HorizontalLayout hlTitleContainer = new HorizontalLayout(spacer, icon, vlTitle);
        hlTitleContainer.setHeight("75px");
        hlTitleContainer.setComponentAlignment(spacer, Alignment.MIDDLE_RIGHT);
        hlTitleContainer.setComponentAlignment(icon, Alignment.MIDDLE_RIGHT);
        hlTitleContainer.setComponentAlignment(vlTitle, Alignment.MIDDLE_RIGHT);

        Label breadcrumb = new Label(MaterialIcons.HOME.getHtml() + " Home / Dashboard", ContentMode.HTML);
        breadcrumb.addStyleName("dark-green-font");
        VerticalLayout breadcrumbContainer = new VerticalLayout(breadcrumb);
        breadcrumbContainer.setHeight("75px");
        breadcrumbContainer.setSpacing(false);
        breadcrumbContainer.setMargin(true);
        breadcrumbContainer.setComponentAlignment(breadcrumb, Alignment.MIDDLE_RIGHT);

        ResponsiveRow row = responsiveLayout.addRow();
        row.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .setComponent(hlTitleContainer);
        row.addColumn()
                .withDisplayRules(3, 3, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .setComponent(new Label());
        row.addColumn()
                .withDisplayRules(3, 3, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .setComponent(new Label());
        row.addColumn()
                .withDisplayRules(3, 3, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .setComponent(breadcrumbContainer);

        vlContainer.addComponent(component);
    }
}
