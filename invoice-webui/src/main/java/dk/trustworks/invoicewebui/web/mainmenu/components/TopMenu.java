package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.vaadin.alump.materialicons.MaterialIcons;

/**
 * Created by hans on 28/08/2017.
 */
@SpringComponent
@SpringUI
public class TopMenu extends CssLayout {

    public TopMenu() {
        setStyleName("v-component-group material");
        setWidth("100%");
        setHeight("75px");

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        this.addComponent(responsiveLayout);

        ResponsiveRow row = responsiveLayout.addRow();
        row.setHorizontalSpacing(false);
        row.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        Image logo = new Image();
        logo.setSource(new ThemeResource("images/logo.png"));
        logo.setSizeFull();
        row.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(logo);

        Button appsButton = new Button(MaterialIcons.APPS);
        appsButton.setStyleName("borderless icon-only h4");

        Button searchButton = new Button(MaterialIcons.SEARCH);
        searchButton.setStyleName("borderless icon-only h4");

        HorizontalLayout horizontalLayout = new HorizontalLayout(appsButton, searchButton);
        row.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withVisibilityRules(true, true, true, true)
                .withComponent(horizontalLayout);

        row.addColumn()
                .withDisplayRules(0, 0, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());

        ResponsiveColumn column = row.addColumn();
        column.setAlignment(ResponsiveColumn.ColumnComponentAlignment.RIGHT);
        column.withDisplayRules(0, 0, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label("user"));
    }
}
