package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by hans on 28/08/2017.
 */

@SpringComponent
@SpringUI
public class LeftMenu extends ResponsiveRow {

    public LeftMenu() {
        this.setMargin(false);
        this.setVerticalSpacing(false);
        this.setHorizontalSpacing(false);
        this.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        MenuItem menuItem1 = new MenuItem();
        menuItem1.getLblMenuitem().setValue("Dashboard");

        MenuItem menuItem2 = new MenuItem();
        menuItem2.getLblMenuitem().setValue("Time Manager");
        menuItem2.addLayoutClickListener(event -> {
            getUI().getNavigator().navigateTo("time");
        });

        MenuItem menuItem3 = new MenuItem();
        menuItem3.getLblMenuitem().setValue("Clients");

        MenuItem menuItem4 = new MenuItem();
        menuItem4.getLblMenuitem().setValue("Projects");

        MenuItem menuItem5 = new MenuItem();
        menuItem5.getLblMenuitem().setValue("Invoice Manager");

        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem1);

        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem2);

        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem3);

        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem4);

        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem5);
    }
}
