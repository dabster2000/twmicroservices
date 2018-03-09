package dk.trustworks.invoicewebui.web.profile.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ProfileCanvas extends VerticalLayout {

    public ProfileCanvas() {
    }

    @Transactional
    public ProfileCanvas init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(null);
        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(null);

        this.addComponent(responsiveLayout);

        return this;
    }
}
