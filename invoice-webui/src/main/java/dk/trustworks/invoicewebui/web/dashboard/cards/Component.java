package dk.trustworks.invoicewebui.web.dashboard.cards;

/**
 * Created by hans on 31/08/2017.
 */
public interface Component {

    int getPriority();

    void setPriority(int priority);

    int getBoxWidth();

    void setBoxWidth(int boxWidth);

    String getName();

    void setName(String name);

    com.vaadin.ui.Component getBoxComponent();
}
