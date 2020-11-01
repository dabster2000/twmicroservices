package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.dashboard.components.BubbleRowDesign;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by hans on 11/08/2017.
 */
public class BubblesCardImpl extends BubblesCardDesign {

    public BubblesCardImpl(BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, PhotoService photoService, Optional<User> user) {
        List<Bubble> bubbles;

        if(user.isPresent()) {
            bubbles = bubbleMemberRepository.findByUseruuid(user.get().getUuid()).stream().map(BubbleMember::getBubble).filter(Bubble::isActive).collect(Collectors.toList());
        } else {
            bubbles = bubbleRepository.findBubblesByActiveTrueOrderByCreatedDesc();
        }

        for (Bubble bubble : bubbles) {
            BubbleRowDesign bubbleRow = new BubbleRowDesign();
            bubbleRow.setWidth(100, Unit.PERCENTAGE);
            bubbleRow.getLblName().setValue(bubble.getName());
            bubbleRow.getTxtMembers().setValue(bubbleMemberRepository.findByBubble(bubble).size()+"");
            bubbleRow.getImgBubblePhoto().setSource(photoService.getRelatedPhotoResource(bubble.getUuid()));
            bubbleRow.getCssOwnerPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getUser(), true, 35, Unit.PIXELS));
            getVlBubbles().addComponent(bubbleRow);
        }

        getImgTop().setSource(new ThemeResource("images/cards/bubbles.jpg"));
        getImgTop().setSizeFull();
    }
}
