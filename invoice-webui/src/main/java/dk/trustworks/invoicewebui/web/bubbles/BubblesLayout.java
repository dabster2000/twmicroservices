package dk.trustworks.invoicewebui.web.bubbles;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
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
import org.vaadin.viritin.button.MButton;

import java.io.ByteArrayInputStream;
import java.util.List;

@SpringComponent
@SpringUI
public class BubblesLayout extends VerticalLayout {

    private UserRepository userRepository;
    private PhotoRepository photoRepository;
    private BubbleRepository bubbleRepository;
    private BubbleMemberRepository bubbleMemberRepository;

    /*
    private ResponsiveRow formRow;
    private ResponsiveRow uploadRow;
    private ResponsiveRow membersRow;

    private Binder<Bubble> bubbleBinder = new Binder<>();
    */

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    //private ResponsiveLayout newBubbleResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow bubblesRow;
    //private ResponsiveRow newBubbleDialogRow;

    @Autowired
    public BubblesLayout(UserRepository userRepository, BubbleRepository bubbleRepository, PhotoRepository photoRepository, BubbleMemberRepository bubbleMemberRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;

        BubbleForm bubbleForm = new BubbleForm(userRepository, bubbleRepository, bubbleMemberRepository, photoRepository);

        responsiveLayout.addRow(bubbleForm.getNewBubbleButton());
        responsiveLayout.addRow(bubbleForm.getDialogRow());

            /*
        responsiveLayout.addRow().addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 10)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 10)
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 2, 2)
                .withComponent(new MButton("blow bubble", event -> newBubbleDialogRow.setVisible(true)).withWidth(100, Unit.PERCENTAGE));
                */
        /*
        ResponsiveRow newBubbleDialogRow = responsiveLayout.addRow();
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
        */

        bubblesRow = responsiveLayout.addRow();
/*
        formRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);
        uploadRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);
        uploadRow.setVisible(false);
        membersRow = newBubbleResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);
        membersRow.setVisible(false);

        //BubbleFormDesign bubbleFormDesign = new BubbleFormDesign();
        TextField bubbleName = new TextField("Bubble name");
        bubbleName.setWidth(100, Unit.PERCENTAGE);
        bubbleBinder.forField(bubbleName).bind(Bubble::getName, Bubble::setName);
        ComboBox<String> applicationType = new ComboBox("Application type");
        applicationType.setWidth(100, Unit.PERCENTAGE);
        applicationType.setEmptySelectionAllowed(false);
        applicationType.setItems("Open", "Invitation", "Closed");
        bubbleBinder.forField(applicationType).bind(Bubble::getApplication, Bubble::setApplication);
        ComboBox<User> bubbleMaster = new ComboBox<>("Bubble master");
        bubbleMaster.setWidth(100, Unit.PERCENTAGE);
        bubbleMaster.setItems(userRepository.findByActiveTrue());
        bubbleMaster.setEmptySelectionAllowed(false);
        bubbleMaster.setItemCaptionGenerator(User::getUsername);
        bubbleBinder.forField(bubbleMaster).bind(Bubble::getUser, Bubble::setUser);
        RichTextArea description = new RichTextArea("Description");
        description.setWidth(100, Unit.PERCENTAGE);
        description.setHeight(300, Unit.PIXELS);
        bubbleBinder.forField(description).bind(Bubble::getDescription, Bubble::setDescription);
        OnOffSwitch active = new OnOffSwitch();
        active.setCaption("Active");
        bubbleBinder.forField(active).bind(Bubble::isActive, Bubble::setActive);
        MButton createButton = new MButton("Blow new bubble!").withWidth(100, Unit.PERCENTAGE).withListener(event -> {
            Bubble bubble = new Bubble();
            try {
                bubbleBinder.writeBean(bubble);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            bubble = bubbleRepository.save(bubble);
            bubbleBinder.readBean(new Bubble());
            formRow.setVisible(false);
            initMembersRow(membersRow, bubble);
            uploadRow.setVisible(true);
            uploadRow.removeAllComponents();
            uploadRow.addColumn()
                    .withDisplayRules(12, 12, 8, 8)
                    .withComponent(new PhotoUploader(bubble.getUuid(), 800, 400, "Upload some cool artwork for the bubble!", photoRepository, this::createFinished).getUploader());
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
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(createButton);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
*/
        this.addComponent(responsiveLayout);
    }

    public void createFinished() {
        System.out.println("BubblesLayout.createFinished");
        //formRow.setVisible(false);
        //uploadRow.setVisible(false);
        //membersRow.setVisible(true);
    }

    private void initMembersRow(ResponsiveRow membersRow, Bubble bubble) {
        membersRow.removeAllComponents();

        List<BubbleMember> bubbleMembers = bubbleMemberRepository.findByBubble(bubble);
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
        twinColSelect.setWidth(100, Unit.PERCENTAGE);
        /*
        ListSelect<User> userListSelect = new ListSelect<>();
        userListSelect.setItems(userRepository.findByActiveTrue());
        userListSelect.setItemCaptionGenerator(User::getUsername);
        userListSelect.setWidth(100, Unit.PERCENTAGE);

        List<BubbleMember> bubbleMembers = bubbleMemberRepository.findByBubble(bubble);
        for (BubbleMember bubbleMember : bubbleMembers) {
            userListSelect.select(bubbleMember.getMember());
        }
        */

        MButton doneButton = new MButton("Done").withWidth(100, Unit.PERCENTAGE).withListener(event -> {
            bubbleMemberRepository.delete(bubbleMemberRepository.findByBubble(bubble));
            for (User user : twinColSelect.getSelectedItems()) {
                bubbleMemberRepository.save(new BubbleMember(user, bubble));
            }
            //formRow.setVisible(true);
            //uploadRow.setVisible(false);
            membersRow.setVisible(false);
            //newBubbleDialogRow.setVisible(false);
            init();
        });
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(twinColSelect, ResponsiveColumn.ColumnComponentAlignment.CENTER);
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(doneButton);
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
    }

    @Transactional
    public BubblesLayout init() {
        loadBubbles();
        return this;
    }

    private void loadBubbles() {
        bubblesRow.removeAllComponents();
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

            //User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();
            User user = userRepository.findByUsername("hans.lassen");

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
            bubblesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(bubblesDesign);
        }
    }
}
