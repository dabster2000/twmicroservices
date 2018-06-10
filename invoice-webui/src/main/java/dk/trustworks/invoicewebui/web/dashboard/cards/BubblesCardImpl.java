package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.dashboard.components.BubbleRowDesign;
import org.vaadin.viritin.label.MLabel;

/**
 * Created by hans on 11/08/2017.
 */
public class BubblesCardImpl extends BubblesCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;

    public BubblesCardImpl(BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, PhotoService photoService, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        for (Bubble bubble : bubbleRepository.findBubblesByActiveTrueOrderByCreatedDesc()) {
            Image bubblePhoto = new Image("", photoService.getRelatedPhoto(bubble.getUuid()));
            bubblePhoto.setHeight(53, Unit.PIXELS);
            bubblePhoto.setWidth(106, Unit.PIXELS);
            MLabel lblName = new MLabel(bubble.getName());
            MLabel lblUsername = new MLabel(bubble.getUser().getUsername());

            BubbleRowDesign bubbleRow = new BubbleRowDesign();
            bubbleRow.getLblName().setValue(bubble.getName());
            bubbleRow.getTxtMembers().setValue(bubbleMemberRepository.findByBubble(bubble).size()+"");
            bubbleRow.getImgBubblePhoto().setSource(photoService.getRelatedPhoto(bubble.getUuid()));
            bubbleRow.getCssOwnerPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getUser(), true, 35));

            getVlBubbles().addComponent(bubbleRow);
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
