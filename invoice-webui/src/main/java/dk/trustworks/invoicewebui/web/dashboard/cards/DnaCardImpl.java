package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;

/**
 * Created by hans on 11/08/2017.
 */
public class DnaCardImpl extends DnaCardDesign implements Component {

    private int priority;
    private int boxWidth;
    private String name;

    public DnaCardImpl(int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        getImgTop().setSource(new ThemeResource("images/cards/dna.jpg"));
        getImgTop().setSizeFull();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public com.vaadin.ui.Component getBoxComponent() {
        return this;
    }

}
