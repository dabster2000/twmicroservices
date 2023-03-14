package dk.trustworks.invoicewebui.web.bubbles;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsInviteRequest;
import com.slack.api.methods.request.conversations.ConversationsKickRequest;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.BubbleType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.BubbleService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.bubbles.components.BubbleForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

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

    private MethodsClient bubbleWebApiClient;

    private MethodsClient bubbleUserBotClient;

    @Autowired
    public BubblesLayout(UserService userService, PhotoService photoService, BubbleService bubbleService) {
        this.userService = userService;
        this.photoService = photoService;
        this.bubbleService = bubbleService;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public BubblesLayout init(BubbleType... types) {
        bubbleWebApiClient = Slack.getInstance().methods(bubbleSlackToken);
        bubbleUserBotClient = Slack.getInstance().methods(bubbleBotUserSlackToken);
        bubbleForm = new BubbleForm(userService, bubbleService, photoService, bubbleWebApiClient);

        responsiveLayout.removeAllComponents();
        responsiveLayout.addRow(bubbleForm.getNewBubbleButton());
        responsiveLayout.addRow(bubbleForm.getDialogRow());

        bubblesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadBubbles(types);
        return this;
    }

    private void loadBubbles(BubbleType... bubbleTypes) {
        bubblesRow.removeAllComponents();
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
        BubbleType bubbleType = null;

        for (Bubble bubble : bubbleService.findBubblesByActiveTrueOrderByCreatedDesc().stream().sorted(Comparator.comparing(Bubble::getType).thenComparing(Bubble::getCreated)).collect(Collectors.toList())) {
            if(!Arrays.asList(bubbleTypes).contains(bubble.getType())) continue;
            if(bubbleType == null || !bubbleType.equals(bubble.getType())) {
                BoxImpl box = new BoxImpl().instance(
                                new MVerticalLayout(
                                        new MLabel(bubble.getType().getName().toUpperCase()).withFullWidth().withStyleName("turquoise-font", "h4", "center-label", "bold", "wrap-label")).withFullWidth().withDefaultComponentAlignment(Alignment.MIDDLE_CENTER))
                        .withBgStyle("dark-grey");
                bubblesRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(box);
                bubbleType = bubble.getType();
            }
            BubblesDesign bubblesDesign = new BubblesDesign();

            bubblesDesign.getLblHeading().setValue(bubble.getName());
            bubblesDesign.getLblDescription().setValue(bubble.getDescription());
            bubblesDesign.getLblMeetingform().setValue(bubble.getMeetingform().equalsIgnoreCase("")?"Not determined yet":bubble.getMeetingform());
            bubblesDesign.getLblPreconditions().setValue(bubble.getPreconditions().equalsIgnoreCase("")?"None":bubble.getPreconditions());

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
                ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                        .channel(owner.getSlackusername()) // Use a channel ID `C1234567` is preferrable
                        .text("Hi "+owner.getFirstname()+", *"+user.getUsername()+"* would like to join your bubble "+bubble.getName()+"!")
                        .build();

                try {
                    bubbleUserBotClient.chatPostMessage(request);
                } catch (IOException | SlackApiException e) {
                    e.printStackTrace();
                }

                Notification.show("You have now applied for membership. The bubble owner will get back to you soon!", Notification.Type.ASSISTIVE_NOTIFICATION);
            });

            bubblesDesign.getBtnJoin().addClickListener(event -> {
                bubbleService.addBubbleMember(bubble.getUuid(), user.getUuid());
                try {
                    bubbleWebApiClient.conversationsInvite(ConversationsInviteRequest.builder().channel(bubble.getSlackchannel()).users(Collections.singletonList(user.getSlackusername())).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Page.getCurrent().reload();
            });

            bubblesDesign.getBtnLeave().addClickListener(event -> {
                bubbleService.removeFromBubble(bubble.getUuid(), user.getUuid());
                try {
                    bubbleWebApiClient.conversationsKick(ConversationsKickRequest.builder().channel(bubble.getSlackchannel()).user(user.getSlackusername()).build());
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
            if(bubble.getOwner().equals(user.getUuid()) || (bubble.getCoowner() != null && bubble.getCoowner().equals(user.getUuid())) || user.getUuid().equals("7948c5e8-162c-4053-b905-0f59a21d7746")) {
                bubblesDesign.getBtnEdit().setVisible(true);
                bubblesDesign.getBtnApply().setVisible(false);
                bubblesDesign.getBtnJoin().setVisible(false);
                bubblesDesign.getBtnLeave().setVisible(false);
            }

            //List<User> users = userService.findAll(true);
            bubblesDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getOwner(), 1));
            if(bubble.getCoowner()!=null) bubblesDesign.getPhotoContainer().addComponent(photoService.getRoundMemberImage(bubble.getCoowner(), 2));
            for (BubbleMember member : bubble.getBubbleMembers()) {
                if(member.getUseruuid().equals(bubble.getOwner()) || (bubble.getCoowner() != null && bubble.getCoowner().equals(user.getUuid()))) continue;
                if(userService.isEmployed(member.getUseruuid())) {
                    Image image = photoService.getRoundMemberImage(member.getUseruuid(), 0);
                    bubblesDesign.getPhotoContainer().addComponent(image);
                }
            }
            String relatedUUID = bubble.getUuid();
            Resource resource = photoService.getRelatedPhotoResource(relatedUUID);

            bubblesDesign.getImgTop().setSource(resource);
            if(bubble.getOwner().equals(user.getUuid()) || (bubble.getCoowner() != null && bubble.getCoowner().equals(user.getUuid())) || user.getUsername().equals("hans.lassen")) {
                bubblesDesign.getImgTop().addClickListener(event -> bubbleForm.editPhotoAction(bubble));
            }
            /*
            Number[] activity = new Number[60];
            for (int i = 0; i < 60; i++) {
                activity[i] = 0;
            }
            try {
                for (Message message : bubbleWebApiClient.conversationsHistory(ConversationsHistoryRequest.builder().channel(bubble.getSlackchannel()).limit(100).build()).getMessages()) {
                    if (message.getSubtype() != null) continue;
                    Instant epochMilli = Instant.ofEpochMilli(Long.parseLong(message.getTs().split("\\.")[0]) * 1000L);
                    LocalDate date = LocalDateTime.ofInstant(epochMilli, ZoneOffset.UTC).toLocalDate();
                    if (DAYS.between(date, LocalDate.now()) > 59) continue;
                    activity[(int) DAYS.between(date, LocalDate.now())] = (activity[(int) DAYS.between(date, LocalDate.now())].intValue() + 1);
                }
            } catch (SlackApiException | IOException | NullPointerException e) {
                System.err.println(e);
            }

            bubblesDesign.getGaugeContainer().addComponent(ActivityGauge.getChart(activity));
             */
            if(bubbleType.equals(BubbleType.KNOWLEDGE)) {
                bubblesRow.addColumn().withDisplayRules(12, 12, 6, 4).withComponent(bubblesDesign);
            } else {
                bubblesRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(bubblesDesign);
            }
        }
    }
}
