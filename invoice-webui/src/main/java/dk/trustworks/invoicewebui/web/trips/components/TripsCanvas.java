package dk.trustworks.invoicewebui.web.trips.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.KnowledgePreloader;
import dk.trustworks.invoicewebui.web.knowledge.components.VideoCardDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SpringComponent
@SpringUI
public class TripsCanvas extends VerticalLayout {

    @Autowired
    private KnowledgePreloader knowledgePreloader;

    @Transactional
    public TripsCanvas init() {
        this.removeAllComponents();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        ResponsiveRow cardRow = responsiveLayout.addRow();

        VideoCardDesign videoCardDesign = new VideoCardDesign();
        videoCardDesign.setWidth("100%");
        BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource("https://vimeopro.com/user71634519/trustworks-trips"));
        tripVideoBrowser.setWidth("100%");
        tripVideoBrowser.setHeight("700px");
        videoCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

        cardRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(videoCardDesign);

        this.addComponent(responsiveLayout);
        return this;
    }
}
