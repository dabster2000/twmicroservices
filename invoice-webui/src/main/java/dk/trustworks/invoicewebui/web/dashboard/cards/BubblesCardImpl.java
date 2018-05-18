package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import org.vaadin.viritin.label.MLabel;

/**
 * Created by hans on 11/08/2017.
 */
public class BubblesCardImpl extends BubblesCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;

    public BubblesCardImpl(BubbleRepository bubbleRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        getGridBubbles().addComponent(new MLabel("Bubble").withStyleName("h4"), 0, 0);
        getGridBubbles().addComponent(new MLabel("Blower").withStyleName("h4"), 1, 0);

        int row = 1;
        for (Bubble bubble : bubbleRepository.findBubblesByActiveTrueOrderByCreatedDesc()) {
            getGridBubbles().setRows(row+1);
            getGridBubbles().addComponent(new Label(bubble.getName()), 0, row);
            getGridBubbles().addComponent(new Label(bubble.getUser().getUsername()), 1, row);
            row++;
        }

        getImgTop().setSource(new ThemeResource("images/cards/bubbles.jpg"));
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
    public Component getBoxComponent() {
        return this;
    }

}
