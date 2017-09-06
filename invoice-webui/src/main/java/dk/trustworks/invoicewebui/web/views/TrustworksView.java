package dk.trustworks.invoicewebui.web.views;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;

/**
 * Created by hans on 06/09/2017.
 */
public interface TrustworksView extends View {

    String getViewName();
    String getViewBreadcrumb();
    FontIcon getViewIcon();

}
