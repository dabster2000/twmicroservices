package dk.trustworks.invoicewebui.web.trips.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.knowledge.components.VideoCardDesign;

@SpringComponent
@SpringUI
public class SalesVideoCanvas extends VerticalLayout {

    public SalesVideoCanvas init() {
        this.removeAllComponents();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        ResponsiveRow cardRow = responsiveLayout.addRow();

        VideoCardDesign videoCardDesign = new VideoCardDesign();
        videoCardDesign.setWidth("100%");
        BrowserFrame videoBrowser = new BrowserFrame(null, new ExternalResource("https://vimeopro.com/user71634519/sales-war-stories"));
        videoBrowser.setWidth("100%");
        videoBrowser.setHeight("700px");
        videoCardDesign.getIframeHolder().addComponent(videoBrowser);

        cardRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(videoCardDesign);

        this.addComponent(responsiveLayout);
        return this;
    }
}
