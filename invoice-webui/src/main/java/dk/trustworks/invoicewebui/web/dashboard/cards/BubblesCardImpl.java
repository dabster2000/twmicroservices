package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.BubbleService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.dashboard.components.BubbleRowDesign;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 11/08/2017.
 */
public class BubblesCardImpl extends BubblesCardDesign {

    public BubblesCardImpl(BubbleService bubbleService, PhotoService photoService, Optional<User> user) {
        List<Bubble> bubbles;

        if(user.isPresent()) {
            bubbles = bubbleService.findByUseruuid(user.get().getUuid());
        } else {
            bubbles = bubbleService.findBubblesByActiveTrueOrderByCreatedDesc();
        }

        for (Bubble bubble : bubbles) {
            System.out.println("bubble.getName() = " + bubble.getName());
            System.out.println("bubble.getBubbleMembers() = " + bubble.getBubbleMembers());
            System.out.println("bubble.getBubbleMembers().size() = " + bubble.getBubbleMembers().size());
            BubbleRowDesign bubbleRow = new BubbleRowDesign();
            bubbleRow.setWidth(100, Unit.PERCENTAGE);
            bubbleRow.getLblName().setValue(bubble.getName());
            bubbleRow.getTxtMembers().setValue(bubble.getBubbleMembers().size()+"");
            bubbleRow.getImgBubblePhoto().setSource(photoService.getRelatedPhotoResource(bubble.getUuid()));
            bubbleRow.getCssOwnerPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getOwner(), true, 35, Unit.PIXELS));
            getVlBubbles().addComponent(bubbleRow);
        }

        getImgTop().setSource(new ThemeResource("images/cards/bubbles.jpg"));
        getImgTop().setSizeFull();
    }
}
