package dk.trustworks.invoicewebui.web.academy.components;

import allbegray.slack.type.Group;
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;
import java.util.stream.Collectors;

public class CourseForm {

    private final UserService userService;
    private final MicroCourseRepository microCourseRepository;
    private final MicroCourseStudentRepository microCourseStudentRepository;
    private final PhotoRepository photoRepository;

    private final ResponsiveRow newCourseDialogRow;

    private final ResponsiveLayout newCourseResponsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

    private Binder<MicroCourse> microCourseBinder = new Binder<>();

    public CourseForm(UserService userService, MicroCourseRepository microCourseRepository, MicroCourseStudentRepository microCourseStudentRepository, PhotoRepository photoRepository) {
        this.userService = userService;
        this.microCourseRepository = microCourseRepository;
        this.microCourseStudentRepository = microCourseStudentRepository;
        this.photoRepository = photoRepository;
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
                .withComponent(new MButton("new micro course", event -> {
                    newCourseDialogRow.setVisible(true);
                    UI.getCurrent().scrollIntoView(newCourseDialogRow);
                    createFormRow(null, newCourse -> createUploadRow(newCourse, newCourse2 -> closeDialogRow()));
                }).withWidth(100, Sizeable.Unit.PERCENTAGE));
        return row;
    }

    public void editPhotoAction(MicroCourse microCourse) {
        newCourseDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newCourseDialogRow);
        createUploadRow(microCourse, newCourse -> closeDialogRow());
    }

    public void editFormAction(MicroCourse microCourse) {
        newCourseDialogRow.setVisible(true);
        UI.getCurrent().scrollIntoView(newCourseDialogRow);
        createFormRow(microCourse,  newCourse2 -> closeDialogRow());
    }

    public void closeDialogRow() {
        newCourseDialogRow.setVisible(false);
        Page.getCurrent().reload();
    }

    private void createUploadRow(final MicroCourse prevCourse, Next next) {
        newCourseResponsiveLayout.removeAllComponents();
        ResponsiveRow uploadRow = newCourseResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        uploadRow.setVisible(true);
        uploadRow.removeAllComponents();
        uploadRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(new PhotoUploader(prevCourse.getUuid(), 800, 400, "Upload some cool artwork for the course! It must be a JPG file thats atleast 800px wide and 400px heigh.", PhotoUploader.Step.UPLOAD, photoRepository, () -> {
                    newCourseResponsiveLayout.removeAllComponents();
                    next.next(prevCourse);
                }).getUploader());
    }

    private void createStudentsRow(final MicroCourse prevCourse, Next next) {
        newCourseResponsiveLayout.removeAllComponents();
        microCourseBinder.readBean(new MicroCourse());
        ResponsiveRow membersRow = newCourseResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        List<MicroCourseStudent> students = microCourseStudentRepository.findByMicroCourse(prevCourse);
        User[] currentUsers = new User[students.size()];
        int i = 0;
        for (MicroCourseStudent student : students) {
            currentUsers[i++] = student.getMember();
        }

        TwinColSelect<User> twinColSelect = new TwinColSelect<>();
        twinColSelect.setItems(userService.findCurrentlyEmployedUsers());
        twinColSelect.select(currentUsers);
        twinColSelect.setRows(12);
        twinColSelect.setLeftColumnCaption("Outside bubble");
        twinColSelect.setRightColumnCaption("In the bubble");
        twinColSelect.setItemCaptionGenerator(User::getUsername);
        twinColSelect.setWidth(100, Sizeable.Unit.PERCENTAGE);

        MButton doneButton = new MButton("Done").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            List<MicroCourseStudent> currentStudents = microCourseStudentRepository.findByMicroCourse(prevCourse);
            microCourseStudentRepository.delete(currentStudents);
            List<User> userList = userService.findCurrentlyEmployedUsers();
            for (User user : twinColSelect.getSelectedItems()) {
                microCourseStudentRepository.save(new MicroCourseStudent(user, prevCourse, "GRADUATED"));
                userList = userList.stream().filter(user2 -> !user2.getUuid().equals(user.getUuid())).collect(Collectors.toList());
            }

            newCourseResponsiveLayout.removeAllComponents();
            if(next!=null) next.next(prevCourse);
        });
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(twinColSelect, ResponsiveColumn.ColumnComponentAlignment.CENTER);
        membersRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(doneButton);
        membersRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(new Label());
    }

    private void createFormRow(final MicroCourse prevMicroCourse, Next next) {
        newCourseResponsiveLayout.removeAllComponents();
        final MicroCourse microCourse = (prevMicroCourse != null)?prevMicroCourse:new MicroCourse();

        ResponsiveRow formRow = newCourseResponsiveLayout.addRow().withHorizontalSpacing(true).withVerticalSpacing(true);

        TextField microCourseName = new TextField("Micro Course name");
        microCourseName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        microCourseBinder.forField(microCourseName).bind(MicroCourse::getName, MicroCourse::setName);
        ComboBox<User> microCourseMaster = new ComboBox<>("Course master");
        microCourseMaster.setWidth(100, Sizeable.Unit.PERCENTAGE);
        microCourseMaster.setItems(userService.findCurrentlyEmployedUsers());
        microCourseMaster.setEmptySelectionAllowed(false);
        microCourseMaster.setItemCaptionGenerator(User::getUsername);
        microCourseBinder.forField(microCourseMaster).bind(MicroCourse::getUser, MicroCourse::setUser);
        RichTextArea description = new RichTextArea("Description");
        description.setWidth(100, Sizeable.Unit.PERCENTAGE);
        description.setHeight(300, Sizeable.Unit.PIXELS);
        microCourseBinder.forField(description).bind(MicroCourse::getDescription, MicroCourse::setDescription);
        OnOffSwitch active = new OnOffSwitch();
        active.setCaption("Active");
        microCourseBinder.forField(active).bind(MicroCourse::isActive, MicroCourse::setActive);

        MButton createButton = new MButton((prevMicroCourse==null)?"Create new course!":"Update course").withWidth(100, Sizeable.Unit.PERCENTAGE).withListener(event -> {
            try {
                microCourseBinder.writeBean(microCourse);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Error saving course", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }

            microCourseRepository.save(microCourse);
            newCourseResponsiveLayout.removeAllComponents();
            if(next!=null) next.next(microCourse);
        });

        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(microCourseName);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(active);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(microCourseMaster);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(description);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        if(prevMicroCourse==null) {
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
            formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        }
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());
        formRow.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(createButton);
        formRow.addColumn().withDisplayRules(12, 12, 3, 3).withComponent(new Label());

        microCourseBinder.readBean(microCourse);
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
        void next(MicroCourse microCourse);
    }
}
