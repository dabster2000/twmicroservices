package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.ui.Component;

/**
 * Created by hans on 31/08/2017.
 */
public interface Box {

    int getPriority();

    void setPriority(int priority);

    int getBoxWidth();

    void setBoxWidth(int boxWidth);

    String getName();

    void setName(String name);

    Component getBoxComponent();
}
