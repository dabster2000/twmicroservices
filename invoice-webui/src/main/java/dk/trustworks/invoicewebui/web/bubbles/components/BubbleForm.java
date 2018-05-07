package dk.trustworks.invoicewebui.web.bubbles.components;

import allbegray.slack.type.Group;
import allbegray.slack.webapi.SlackWebApiClient;
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.BubbleMember;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;
import java.util.stream.Collectors;

public class BubbleForm {

    private final UserRepository userRepository;
    private final BubbleRepository bubbleRepository;
    private final BubbleMemberRepository bubbleMemberRepository;
    private final PhotoRepository photoRepository;

    private SlackWebApiClient motherWebApiClient;

    private final ResponsiveRow newBubbleDialogRow;

    private final ResponsiveLayout newBubbleResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

    private ResponsiveRow formRow;
    private ResponsiveRow uploadRow;
    private ResponsiveRow membersRow;

    private Binder<Bubble> bubbleBinder = new Binder<>();

    public BubbleForm(UserRepository userRepository, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, PhotoRepository photoRepository, SlackWebApiClient motherWebApiClient) {
        this.userRepository = userRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.photoRepository = photoRepository;
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
                    createFormRow(null, newBubble -> createUploadRow(newBubble, newBubble2 -> createMembersRow(newBubble2, newBubble3 -> closeDialogRow())));
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
        uploadRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        uploadRow.setVisible(true);
        uploadRow.removeAllComponents();
        uploadRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(new PhotoUploader(prevBubble.getUuid(), 800, 400, "Upload some cool artwork for the bubble!", PhotoUploader.Step.UPLOAD, photoRepository, () -> next.next(prevBubble)).getUploader());
    }

    private void createMembersRow(final Bubble prevBubble, Next next) {
        newBubbleResponsiveLayout.removeAllComponents();
        bubbleBinder.readBean(new Bubble());
        membersRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        List<BubbleMember> bubbleMembers = bubbleMemberRepository.findByBubble(prevBubble);
        User[] currentUsers = new User[bubbleMembers.size()];
        int i = 0;
        for (BubbleMember bubbleMember : bubbleMembers) {
            currentUsers[i++] = bubbleMember.getMember();
        }

        TwinColSelect<User> twinColSelect = new TwinColSelect<>();
        twinColSelect.setItems(userRepository.findByActiveTrue());
        twinColSelect.select(currentUsers);
        twinColSelect.setRows(12);
        twinColSelect.setLeftColumnCaption("Outside bubble");
        twinColSelect.setRightColumnCaption("In the bubble");
        twinColSelect.setItemCaptionGenerator(User::getUsername);
        twinColSelect.setWidth(100, Sizeable.Unit.PERCENTAGE);

        MButton doneButton = new MButton("Done").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            Group channel = motherWebApiClient.getGroupInfo(prevBubble.getSlackchannel());
            List<String> currentSlackMembers = channel.getMembers();
            List<BubbleMember> currentBubbleMembers = bubbleMemberRepository.findByBubble(prevBubble);
            bubbleMemberRepository.delete(currentBubbleMembers);
            List<User> userList = userRepository.findByActiveTrue();
            for (User user : twinColSelect.getSelectedItems()) {
                bubbleMemberRepository.save(new BubbleMember(user, prevBubble));
                try {
                    if (!currentSlackMembers.contains(user.getSlackusername()))
                        motherWebApiClient.inviteUserToGroup(channel.getId(), user.getSlackusername());
                } catch (Exception e) {
                    System.out.println("failed join user.getUsername() = " + user.getUsername());
                    e.printStackTrace();
                }
                userList = userList.stream().filter(user2 -> !user2.getUuid().equals(user.getUuid())).collect(Collectors.toList());
            }
            for (User user : userList) {
                try {
                    if(currentSlackMembers.contains(user.getSlackusername())) motherWebApiClient.kickUserFromGroup(channel.getId(), user.getSlackusername());
                } catch (Exception e) {
                    System.out.println("failed kick user.getUsername() = " + user.getUsername());
                    e.printStackTrace();
                }
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
        System.out.println("BubbleForm.createFormRow");
        newBubbleResponsiveLayout.removeAllComponents();
        final Bubble bubble = (prevBubble != null)?prevBubble:new Bubble();

        formRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        TextField bubbleName = new TextField("Bubble name");
        bubbleName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        bubbleBinder.forField(bubbleName).bind(Bubble::getName, Bubble::setName);
        ComboBox<String> applicationType = new ComboBox("Application type");
        applicationType.setWidth(100, Sizeable.Unit.PERCENTAGE);
        applicationType.setEmptySelectionAllowed(false);
        applicationType.setItems("Open", "Invitation", "Closed");
        bubbleBinder.forField(applicationType).bind(Bubble::getApplication, Bubble::setApplication);
        ComboBox<User> bubbleMaster = new ComboBox<>("Bubble master");
        bubbleMaster.setWidth(100, Sizeable.Unit.PERCENTAGE);
        bubbleMaster.setItems(userRepository.findByActiveTrue());
        bubbleMaster.setEmptySelectionAllowed(false);
        bubbleMaster.setItemCaptionGenerator(User::getUsername);
        bubbleBinder.forField(bubbleMaster).bind(Bubble::getUser, Bubble::setUser);
        RichTextArea description = new RichTextArea("Description");
        description.setWidth(100, Sizeable.Unit.PERCENTAGE);
        description.setHeight(300, Sizeable.Unit.PIXELS);
        bubbleBinder.forField(description).bind(Bubble::getDescription, Bubble::setDescription);
        OnOffSwitch active = new OnOffSwitch();
        active.setCaption("Active");
        bubbleBinder.forField(active).bind(Bubble::isActive, Bubble::setActive);
        /*
        ComboBox<Group> cbSlackChannel = new ComboBox<>();
        cbSlackChannel.setItems(motherWebApiClient.getGroupList(true));
        cbSlackChannel.setItemCaptionGenerator(Group::getName);
        cbSlackChannel.setEmptySelectionAllowed(true);
        cbSlackChannel.setEmptySelectionCaption("new slack channel");
        */
        TextField slackChannelName = new TextField("Channel name");
        slackChannelName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        slackChannelName.setMaxLength(19);

        MButton createButton = new MButton((prevBubble==null)?"Blow new bubble!":"Update bubble").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            try {
                bubbleBinder.writeBean(bubble);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            if(prevBubble==null) {
                Group group = motherWebApiClient.createGroup("b_" + slackChannelName.getValue().trim().toLowerCase());
                bubble.setSlackchannel(group.getId());
            } else if(!bubble.isActive()) {
                motherWebApiClient.archiveGroup(prevBubble.getSlackchannel());
            }

            bubbleRepository.save(bubble);
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
/*
        final Optional<Group> optGroup = motherWebApiClient.getGroupList(true).stream().filter(channel -> channel.getId().equals(bubble.getSlackchannel())).findFirst();
        if(optGroup.isPresent()) {
            cbSlackChannel.setValue(optGroup.get());
        }
        */
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
