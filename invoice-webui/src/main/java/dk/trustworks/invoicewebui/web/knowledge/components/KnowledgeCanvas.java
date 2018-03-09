package dk.trustworks.invoicewebui.web.knowledge.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.KnowledgePreloader;
import dk.trustworks.invoicewebui.model.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringComponent
@SpringUI
public class KnowledgeCanvas extends VerticalLayout {

    @Autowired
    private KnowledgePreloader knowledgePreloader;

    @Transactional
    public KnowledgeCanvas init() {
        this.removeAllComponents();
        Map<Faq, ResponsiveColumn> cardColumns = new HashMap<>();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
/*
        ResponsiveRow searchRow = responsiveLayout.addRow();
        searchRow.addColumn().withDisplayRules(12, 12, 4, 4)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 1)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 1)
                .withComponent(new MTextField("filter")
                        .withFullWidth()
                        /*
                        .addTextChangeListener(event -> {
                            for (Faq faq : cardColumns.keySet()) {
                                if (faq.getTitle().length() == 0 || faq.getContent().length() == 0) continue;
                                if (StringUtils.containsIgnoreCase(faq.getTitle(), event.getValue())) {
                                    cardColumns.get(faq).setVisibilityRules(true, true, true, true);
                                } else try {
                                    if (StringUtils.containsIgnoreCase(new String(getDecoder().decode(faq.getContent()), "utf-8"), event.getValue())) {
                                        cardColumns.get(faq).setVisibilityRules(true, true, true, true);
                                    } else {
                                        cardColumns.get(faq).setVisibilityRules(false, false, false, false);
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        );*/

        ResponsiveRow cardRow = responsiveLayout.addRow();

        VideoCardDesign videoCardDesign = new VideoCardDesign();
        videoCardDesign.setWidth("100%");
        BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource("https://vimeopro.com/user71634519/trustworks-knowledge"));
        tripVideoBrowser.setWidth("100%");
        tripVideoBrowser.setHeight("700px");
        videoCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

        cardRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(videoCardDesign);
/*
        for (String videoUrl : knowledgePreloader.getTrustworksKnowledge()) {
            VideoCardDesign videoCardDesign = new VideoCardDesign();
            videoCardDesign.setWidth("100%");
            //Embedded tripVideoBrowser = new Embedded("", new ExternalResource(videoUrl));
            BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource(videoUrl));
            tripVideoBrowser.setWidth("100%");
            tripVideoBrowser.setHeight("350px");
            videoCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

            cardRow.addColumn()
                    .withDisplayRules(12, 12, 6, 6)
                    .withComponent(videoCardDesign);
        }
*/
        this.addComponent(responsiveLayout);
        return this;
    }
}
