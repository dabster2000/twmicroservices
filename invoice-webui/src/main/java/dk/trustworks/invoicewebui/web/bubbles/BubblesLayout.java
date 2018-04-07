package dk.trustworks.invoicewebui.web.bubbles;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.bubbles.components.BubbleForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@SpringComponent
@SpringUI
public class BubblesLayout extends VerticalLayout {

    private UserRepository userRepository;
    private PhotoRepository photoRepository;
    private BubbleRepository bubbleRepository;
    private BubbleMemberRepository bubbleMemberRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow bubblesRow;

    private BubbleForm bubbleForm;

    @Autowired
    public BubblesLayout(UserRepository userRepository, BubbleRepository bubbleRepository, PhotoRepository photoRepository, BubbleMemberRepository bubbleMemberRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;

        bubbleForm = new BubbleForm(userRepository, bubbleRepository, bubbleMemberRepository, photoRepository);

        responsiveLayout.addRow(bubbleForm.getNewBubbleButton());
        responsiveLayout.addRow(bubbleForm.getDialogRow());

        bubblesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);
    }

    @Transactional
    public BubblesLayout init() {
        loadBubbles();
        return this;
    }

    private void loadBubbles() {
        bubblesRow.removeAllComponents();
        //User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        User user = userRepository.findByUsername("hans.lassen");

        for (Bubble bubble : bubbleRepository.findBubblesByActiveTrue()) {
            BubblesDesign bubblesDesign = new BubblesDesign();

            bubblesDesign.getLblHeading().setValue(bubble.getName());
            bubblesDesign.getLblDescription().setValue(bubble.getDescription());

            bubblesDesign.getBtnLeave().setVisible(false);
            bubblesDesign.getBtnEdit().setVisible(false);
            bubblesDesign.getBtnApply().setVisible(false);
            bubblesDesign.getBtnJoin().setVisible(false);

            if(bubble.getApplication().equals("Open")) {
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(true);
            }
            if(bubble.getApplication().equals("Closed")) {
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
            }
            if(bubble.getApplication().equals("Invitation")) {
                bubblesDesign.getBtnApply().setVisible(true);
                bubblesDesign.getBtnJoin().setVisible(false);
            }

            bubblesDesign.getBtnEdit().addClickListener(event -> {
                bubbleForm.editFormAction(bubble);
            });

            if(bubbleMemberRepository.findByBubbleAndMember(bubble, user) != null) {
                bubblesDesign.getBtnLeave().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
            }
            if(bubble.getUser().getUuid().equals(user.getUuid())) {
                bubblesDesign.getBtnEdit().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
                bubblesDesign.getBtnLeave().setVisible(false);
            }

            for (BubbleMember member : bubbleMemberRepository.findByBubble(bubble)) {
                Photo photo = photoRepository.findByRelateduuid(member.getMember().getUuid());

                Image image = new Image(null,
                        new StreamResource((StreamResource.StreamSource) () ->
                                new ByteArrayInputStream(photo.getPhoto()),
                                member.getMember().getUsername()+System.currentTimeMillis()+".jpg"));
                image.setStyleName("img-circle");
                image.setWidth(75, Unit.PIXELS);
                image.setHeight(75, Unit.PIXELS);
                bubblesDesign.getPhotoContainer().addComponent(image);
            }

            Photo bubblephoto = photoRepository.findByRelateduuid(bubble.getUuid());
            bubblesDesign.getImgTop().setSource(
                    new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(bubblephoto.getPhoto()),
                    bubble.getName()+System.currentTimeMillis()+".jpg"));
            if(bubble.getUser().getUuid().equals(user.getUuid())) {
                bubblesDesign.getImgTop().addClickListener(event -> bubbleForm.editPhotoAction(bubble));
            }
            bubblesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(bubblesDesign);
        }
    }
}
