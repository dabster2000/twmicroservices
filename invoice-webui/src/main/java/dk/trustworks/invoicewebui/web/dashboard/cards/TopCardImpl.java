package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;

public class TopCardImpl extends TopCardDesign {

    public TopCardImpl(TopCardContent topCardContent) {
        getImgIcon().setSource(new ThemeResource(topCardContent.getIcon()));
        getLblNumber().setValue(topCardContent.getNumber());
        getLblTitle().setValue(topCardContent.getTitle());
        getLblSubtitle().setValue(topCardContent.getSubTitle());
        getCardHolder().addStyleName(topCardContent.getStyle());
    }
}
