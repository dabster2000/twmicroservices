package dk.trustworks.invoicewebui.web.academy.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.CkoCourse;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.MicroCourseRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class CourseForm {

    private final String type;

    private final UserService userService;
    private final MicroCourseRepository microCourseRepository;
    private final PhotoService photoService;

    private final ResponsiveRow newCourseDialogRow;

    private final ResponsiveLayout newCourseResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

    private Binder<CkoCourse> courseBinder = new Binder<>();

    public CourseForm(String type, UserService userService, MicroCourseRepository microCourseRepository, PhotoService photoService) {
        this.type = type;
        this.userService = userService;
        this.microCourseRepository = microCourseRepository;
        this.photoService = photoService;
        newCourseDialogRow = getDialogRow(newCourseResponsiveLayout);
    }

    public ResponsiveRow getDialogRow() {
        return newCourseDialogRow;
    }

    public ResponsiveRow getNewCourseButton() {
        ResponsiveRow row = new ResponsiveRow();
        row.addColumn()
                .withOffset(ResponsiveLayout.DisplaySize.MD, 10)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 10)
                .withVisibilityRules(false, false, true, true)
                .withDisplayRules(12, 12, 2, 2)
                .withComponent(new MVerticalLayout(new MButton("New course", event -> {
                    newCourseDialogRow.setVisible(true);
                    UI.getCurrent().scrollIntoView(newCourseDialogRow);
                    createFormRow(null, newCourse -> createUploadRow(newCourse, newCourse2 -> closeDialogRow()));
                }).withWidth(100, Sizeable.Unit.PERCENTAGE)));
        return row;
    }

    public void editPhotoAction(CkoCourse ckoCourse) {
        newCourseDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newCourseDialogRow);
        createUploadRow(ckoCourse, newCourse -> closeDialogRow());
    }

    public void editFormAction(CkoCourse ckoCourse) {
        newCourseDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newCourseDialogRow);
        createFormRow(ckoCourse, newCourse2 -> closeDialogRow());
    }

    public void closeDialogRow() {
        newCourseDialogRow.setVisible(false);
        Page.getCurrent().reload();
    }

    private void createUploadRow(final CkoCourse prevCourse, Next next) {
        newCourseResponsiveLayout.removeAllComponents();
        ResponsiveRow uploadRow = newCourseResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        uploadRow.setVisible(true);
        uploadRow.removeAllComponents();
        uploadRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(new PhotoUploader(prevCourse.getUuid(), 800, 400, "Upload some cool artwork for the course! It must be a JPG file thats atleast 800px wide and 400px heigh.", PhotoUploader.Step.UPLOAD, photoService, () -> {
                    newCourseResponsiveLayout.removeAllComponents();
                    next.next(prevCourse);
                }).getUploader());
    }

    private void createFormRow(final CkoCourse prevCkoCourse, Next next) {
        newCourseResponsiveLayout.removeAllComponents();
        final CkoCourse ckoCourse = (prevCkoCourse != null)? prevCkoCourse : new CkoCourse(type);

        ResponsiveRow formRow = newCourseResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        TextField courseName = new TextField("Course name");
        courseName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        courseBinder.forField(courseName).bind(CkoCourse::getName, CkoCourse::setName);
        ComboBox<User> courseMaster = new ComboBox<>("Course master");
        courseMaster.setWidth(100, Sizeable.Unit.PERCENTAGE);
        courseMaster.setItems(userService.findCurrentlyEmployedUsers(true));
        courseMaster.setEmptySelectionAllowed(false);
        courseMaster.setItemCaptionGenerator(User::getUsername);
        courseBinder.forField(courseMaster).bind(CkoCourse::getUser, CkoCourse::setUser);
        RichTextArea description = new RichTextArea("Description");
        description.setWidth(100, Sizeable.Unit.PERCENTAGE);
        description.setHeight(300, Sizeable.Unit.PIXELS);
        courseBinder.forField(description).bind(CkoCourse::getDescription, CkoCourse::setDescription);
        OnOffSwitch active = new OnOffSwitch();
        active.setCaption("Active");
        courseBinder.forField(active).bind(CkoCourse::isActive, CkoCourse::setActive);

        MButton createButton = new MButton((prevCkoCourse ==null)?"Create new course!":"Update course").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            try {
                courseBinder.writeBean(ckoCourse);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Error saving course", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }

            microCourseRepository.save(ckoCourse);
            newCourseResponsiveLayout.removeAllComponents();
            if(next!=null) next.next(ckoCourse);
        });

        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(courseName);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(active);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(courseMaster);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(description);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        if(prevCkoCourse ==null) {
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        }
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(createButton);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());

        courseBinder.readBean(ckoCourse);
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
        void next(CkoCourse ckoCourse);
    }
}
