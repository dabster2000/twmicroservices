package dk.trustworks.invoicewebui.web.bubbles.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.*;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.BubbleService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BubbleForm {

    private final UserService userService;
    private final BubbleService bubbleService;
    private final PhotoService photoService;

    private final MethodsClient motherWebApiClient;

    private final ResponsiveRow newBubbleDialogRow;

    private final ResponsiveLayout newBubbleResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

    private final Binder<Bubble> bubbleBinder = new Binder<>();

    public BubbleForm(UserService userService, BubbleService bubbleService, PhotoService photoService, MethodsClient motherWebApiClient) {
        this.userService = userService;
        this.bubbleService = bubbleService;
        this.photoService = photoService;
        System.out.println("motherWebApiClient = " + motherWebApiClient);
        this.motherWebApiClient = motherWebApiClient;
        newBubbleDialogRow = getDialogRow(newBubbleResponsiveLayout);
    }

    public ResponsiveRow getDialogRow() {
        return newBubbleDialogRow;
    }

    public ResponsiveRow getNewBubbleButton() {
        System.out.println("BubbleForm.getNewBubbleButton");
        ResponsiveRow row = new ResponsiveRow();
        row.addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 10)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 10)
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 2, 2)
                .withComponent(new MButton("blow bubble", event -> {
                    newBubbleDialogRow.setVisible(true);
                    UI.getCurrent().scrollIntoView(newBubbleDialogRow);
                    //createFormRow(null, newBubble -> createUploadRow(newBubble, newBubble2 -> createMembersRow(newBubble2, newBubble3 -> closeDialogRow())));
                    createFormRow(null, newBubble3 -> closeDialogRow());
                }).withWidth(100, Sizeable.Unit.PERCENTAGE));
        return row;
    }

    public void editPhotoAction(Bubble bubble) {
        newBubbleDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newBubbleDialogRow);
        createUploadRow(bubble, newBubble -> closeDialogRow());
    }

    public void editFormAction(Bubble bubble) {
        newBubbleDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newBubbleDialogRow);
        createFormRow(bubble,  newBubble2 -> createMembersRow(newBubble2, newBubble3 -> closeDialogRow()));
    }

    public void closeDialogRow() {
        newBubbleDialogRow.setVisible(false);
        Page.getCurrent().reload();
    }

    private void createUploadRow(final Bubble prevBubble, Next next) {
        newBubbleResponsiveLayout.removeAllComponents();
        ResponsiveRow uploadRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        uploadRow.setVisible(true);
        uploadRow.removeAllComponents();
        uploadRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(new PhotoUploader(prevBubble.getUuid(), 800, 400, "Upload some cool artwork for the bubble! It must be a PNG file thats atleast 800px wide and 400px heigh.", PhotoUploader.Step.UPLOAD, photoService, () -> next.next(prevBubble)).getUploader());
    }

    private void createMembersRow(final Bubble prevBubble, Next next) {
        newBubbleResponsiveLayout.removeAllComponents();
        bubbleBinder.readBean(new Bubble());
        ResponsiveRow membersRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        List<BubbleMember> bubbleMembers = prevBubble.getBubbleMembers(); //bubbleMemberRepository.findByBubble(prevBubble);
        User[] currentUsers = new User[bubbleMembers.size()];
        int i = 0;
        for (BubbleMember bubbleMember : bubbleMembers) {
            currentUsers[i++] = userService.findByUUID(bubbleMember.getUseruuid(), true);
        }

        TwinColSelect<User> twinColSelect = new TwinColSelect<>();
        twinColSelect.setItems(userService.findCurrentlyEmployedUsers(true));
        twinColSelect.select(currentUsers);
        twinColSelect.setRows(12);
        twinColSelect.setLeftColumnCaption("Outside bubble");
        twinColSelect.setRightColumnCaption("In the bubble");
        twinColSelect.setItemCaptionGenerator(User::getUsername);
        twinColSelect.setWidth(100, Sizeable.Unit.PERCENTAGE);

        MButton doneButton = new MButton("Done").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            try {
                //Conversation channel = motherWebApiClient.conversationsInfo(ConversationsInfoRequest.builder().channel(prevBubble.getSlackchannel()).build()).getChannel();
                List<String> members = motherWebApiClient.conversationsMembers(ConversationsMembersRequest.builder().channel(prevBubble.getSlackchannel()).build()).getMembers();
                //List<String> currentSlackMembers = channel.;
                //List<BubbleMember> currentBubbleMembers = prevBubble.getBubbleMembers();//bubbleMemberRepository.findByBubble(prevBubble);
                bubbleService.removeAllMembers(prevBubble.getUuid());
                List<User> userList = userService.findCurrentlyEmployedUsers(true);
                for (User user : twinColSelect.getSelectedItems()) {
                    bubbleService.addBubbleMember(prevBubble.getUuid(), user.getUuid());
                    try {
                        if (!members.contains(user.getSlackusername()))
                            motherWebApiClient.conversationsInvite(ConversationsInviteRequest.builder().channel(prevBubble.getSlackchannel()).users(Collections.singletonList(user.getSlackusername())).build());
                    //.inviteUserToGroup(channel.getId(), user.getSlackusername());
                    } catch (Exception e) {
                        System.out.println("failed join user.getUsername() = " + user.getUsername());
                        e.printStackTrace();
                    }
                    userList = userList.stream().filter(user2 -> !user2.getUuid().equals(user.getUuid())).collect(Collectors.toList());
                }
                for (User user : userList) {
                    try {
                        if(members.contains(user.getSlackusername())) motherWebApiClient.conversationsKick(ConversationsKickRequest.builder().channel(prevBubble.getSlackchannel()).user(user.getSlackusername()).build());
                    } catch (Exception e) {
                        System.out.println("failed kick user.getUsername() = " + user.getUsername());
                        e.printStackTrace();
                    }
                }
            } catch (IOException | SlackApiException e) {
                e.printStackTrace();
            }

            newBubbleResponsiveLayout.removeAllComponents();
            if(next!=null) next.next(prevBubble);
        });
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(twinColSelect, ResponsiveColumn.ColumnComponentAlignment.CENTER);
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(doneButton);
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
    }

    private void createFormRow(final Bubble prevBubble, Next next) {
        newBubbleResponsiveLayout.removeAllComponents();
        final Bubble bubble = (prevBubble != null)?prevBubble:new Bubble();

        ResponsiveRow formRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        TextField bubbleName = new TextField("Bubble name");
        bubbleName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        bubbleBinder.forField(bubbleName).bind(Bubble::getName, Bubble::setName);
        ComboBox<String> applicationType = new ComboBox<>("Application type");
        applicationType.setWidth(100, Sizeable.Unit.PERCENTAGE);
        applicationType.setEmptySelectionAllowed(false);
        applicationType.setItems("Open", "Invitation");
        applicationType.setDescription("<h2>Application Type</h2>" +
                "<ul>" +
                "<li><b>Open:</b> People can freely enter and exit the bubble</li>" +
                "<li><b>Invitation:</b> The bubble master must actively add and remove people from the bubble</li>" +
                "</ul>", ContentMode.HTML);
        bubbleBinder.forField(applicationType).bind(Bubble::getApplication, Bubble::setApplication);
        ComboBox<User> bubbleMaster = new ComboBox<>("Bubble master");
        bubbleMaster.setWidth(100, Sizeable.Unit.PERCENTAGE);
        List<User> currentlyEmployedUsers = userService.findCurrentlyEmployedUsers(true);
        bubbleMaster.setItems(currentlyEmployedUsers);
        bubbleMaster.setEmptySelectionAllowed(false);
        bubbleMaster.setItemCaptionGenerator(User::getUsername);
        bubbleBinder.forField(bubbleMaster).bind(b -> UserService.GetUserFromUUID(b.getOwner(), currentlyEmployedUsers), (b, u) -> b.setOwner(u.getUuid()));
        RichTextArea description = new RichTextArea("Description");
        description.setWidth(100, Sizeable.Unit.PERCENTAGE);
        description.setHeight(300, Sizeable.Unit.PIXELS);
        bubbleBinder.forField(description).bind(Bubble::getDescription, Bubble::setDescription);
        OnOffSwitch active = new OnOffSwitch();
        active.setCaption("Active");
        bubbleBinder.forField(active).bind(Bubble::isActive, Bubble::setActive);
        TextField slackChannelName = new TextField("Channel name");
        slackChannelName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        slackChannelName.setMaxLength(19);
        slackChannelName.setDescription("The name of the Slack channel, which is created along with the bubble. Remember: No spaces, maximum 19 characters and all lowercase characters");

        MButton createButton = new MButton((prevBubble==null)?"Blow new bubble!":"Update bubble").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            try {
                bubbleBinder.writeBean(bubble);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Error saving bubble", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
            if(prevBubble==null) {
                try {
                    ConversationsCreateResponse group = motherWebApiClient.conversationsCreate(ConversationsCreateRequest.builder().isPrivate(true).name("b_" + slackChannelName.getValue().trim().toLowerCase().replace(" ", "-")).build());
                    bubble.setSlackchannel(group.getChannel().getId());
                } catch (IOException | SlackApiException e) {
                    e.printStackTrace();
                }
            } else if(!bubble.isActive()) {
                try {
                    motherWebApiClient.conversationsArchive(ConversationsArchiveRequest.builder().channel(prevBubble.getSlackchannel()).build());
                } catch (IOException | SlackApiException e) {
                    e.printStackTrace();
                }
            }

            if(prevBubble==null) bubbleService.saveBubble(bubble);
            else bubbleService.updateBubble(bubble);
            newBubbleResponsiveLayout.removeAllComponents();
            if(next!=null) next.next(bubble);
        });

        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(bubbleName);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(active);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(applicationType);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(bubbleMaster);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(description);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        if(prevBubble==null) {
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
            formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(slackChannelName);
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        }
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(createButton);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());

        bubbleBinder.readBean(bubble);
    }

    private ResponsiveRow getDialogRow(ResponsiveLayout newBubbleResponsiveLayout) {
        ResponsiveRow newBubbleDialogRow = new ResponsiveRow();
        newBubbleDialogRow.withHorizontalSpacing(true);
        newBubbleDialogRow.setVisible(false);
        newBubbleDialogRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 2)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 2)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new MVerticalLayout()
                        .withSpacing(false)
                        .withMargin(true)
                        .withStyleName("card-3", "card-no-padding")
                        .withComponent(newBubbleResponsiveLayout));
        return newBubbleDialogRow;
    }

    @FunctionalInterface
    public interface Next {
        void next(Bubble bubble);
    }
}
