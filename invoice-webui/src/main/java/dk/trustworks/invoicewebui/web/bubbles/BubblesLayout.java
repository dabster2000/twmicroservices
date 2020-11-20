package dk.trustworks.invoicewebui.web.bubbles;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.type.Message;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.BubbleService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.bubbles.components.ActivityGauge;
import dk.trustworks.invoicewebui.web.bubbles.components.BubbleForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@SpringComponent
@SpringUI
public class BubblesLayout extends VerticalLayout {

    private final UserService userService;
    private final PhotoService photoService;
    private final BubbleService bubbleService;

    private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow bubblesRow;

    private BubbleForm bubbleForm;

    @Value("${bubbleSlackBotToken}")
    private String bubbleSlackToken;

    @Value("${bubbleSlackBotUserToken}")
    private String bubbleBotUserSlackToken;

    private SlackWebApiClient bubbleWebApiClient;

    private SlackWebApiClient bubbleUserBotClient;

    @Autowired
    public BubblesLayout(UserService userService, PhotoService photoService, BubbleService bubbleService) {
        this.userService = userService;
        this.photoService = photoService;
        this.bubbleService = bubbleService;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public BubblesLayout init() {
        bubbleWebApiClient = SlackClientFactory.createWebApiClient(bubbleSlackToken);
        bubbleUserBotClient = SlackClientFactory.createWebApiClient(bubbleBotUserSlackToken);
        bubbleForm = new BubbleForm(userService, bubbleService, photoService, bubbleWebApiClient);

        responsiveLayout.removeAllComponents();
        responsiveLayout.addRow(bubbleForm.getNewBubbleButton());
        responsiveLayout.addRow(bubbleForm.getDialogRow());

        bubblesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadBubbles();
        return this;
    }

    private void loadBubbles() {
        bubblesRow.removeAllComponents();
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        //User user = userRepository.findByUsername("hans.lassen");

        for (Bubble bubble : bubbleService.findBubblesByActiveTrueOrderByCreatedDesc()) {
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

            bubblesDesign.getBtnEdit().addClickListener(event -> bubbleForm.editFormAction(bubble));
            bubblesDesign.getBtnApply().addClickListener(event -> {
                User owner = userService.findByUUID(bubble.getOwner(), true);
                ChatPostMessageMethod applyMessage = new ChatPostMessageMethod(owner.getSlackusername(), "Hi "+owner.getFirstname()+", *"+user.getUsername()+"* would like to join your bubble "+bubble.getName()+"!");
                applyMessage.setAs_user(true);
                bubbleUserBotClient.postMessage(applyMessage);
                Notification.show("You have now applied for membership. The bubble owner will get back to you soon!", Notification.Type.ASSISTIVE_NOTIFICATION);
            });

            bubblesDesign.getBtnJoin().addClickListener(event -> {
                bubbleService.addBubbleMember(bubble.getUuid(), user.getUuid());
                try {
                    bubbleWebApiClient.inviteUserToGroup(bubbleWebApiClient.getGroupInfo(bubble.getSlackchannel()).getId(), user.getSlackusername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Page.getCurrent().reload();
            });

            bubblesDesign.getBtnLeave().addClickListener(event -> {
                bubbleService.removeFromBubble(bubble.getUuid(), user.getUuid());
                try {
                    bubbleWebApiClient.kickUserFromGroup(bubbleWebApiClient.getGroupInfo(bubble.getSlackchannel()).getId(), user.getSlackusername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Page.getCurrent().reload();
            });

            if(bubble.getBubbleMembers().stream().anyMatch(bubbleMember -> bubbleMember.getUseruuid().equals(user.getUuid()))) {
                bubblesDesign.getBtnLeave().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
            }
            if(bubble.getOwner().equals(user.getUuid())) {
                bubblesDesign.getBtnEdit().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
                bubblesDesign.getBtnLeave().setVisible(false);
            }

            List<User> users = userService.findAll(true);
            bubblesDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getOwner(), true));
            for (BubbleMember member : bubble.getBubbleMembers()) {
                if(member.getUseruuid().equals(bubble.getOwner())) continue;
                if(userService.isEmployed(member.getUseruuid())) {
                    Image image = photoService.getRoundMemberImage(bubble.getOwner(), false);
                    bubblesDesign.getPhotoContainer().addComponent(image);
                }
            }
            String relatedUUID = bubble.getUuid();
            Resource resource = photoService.getRelatedPhotoResource(relatedUUID);

            bubblesDesign.getImgTop().setSource(resource);
            if(bubble.getOwner().equals(user.getUuid()) || user.getUsername().equals("hans.lassen")) {
                bubblesDesign.getImgTop().addClickListener(event -> bubbleForm.editPhotoAction(bubble));
            }

            Number[] activity = new Number[60];
            for (int i = 0; i < 60; i++) {
                activity[i] = 0;
            }
            try {
                for (Message message : bubbleWebApiClient.getGroupHistory(bubble.getSlackchannel(), 100).getMessages()) {
                    if (message.getSubtype() != null) continue;
                    Instant epochMilli = Instant.ofEpochMilli(Long.parseLong(message.getTs().split("\\.")[0]) * 1000L);
                    LocalDate date = LocalDateTime.ofInstant(epochMilli, ZoneOffset.UTC).toLocalDate();
                    if (DAYS.between(date, LocalDate.now()) > 59) continue;
                    activity[(int) DAYS.between(date, LocalDate.now())] = (activity[(int) DAYS.between(date, LocalDate.now())].intValue() + 1);
                }
            }catch (SlackResponseErrorException e) {
                e.printStackTrace();
            }

            bubblesDesign.getGaugeContainer().addComponent(ActivityGauge.getChart(activity));

            bubblesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(bubblesDesign);
        }
    }
}
