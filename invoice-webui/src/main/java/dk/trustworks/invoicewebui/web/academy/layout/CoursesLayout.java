package dk.trustworks.invoicewebui.web.academy.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.MicroCourse;
import dk.trustworks.invoicewebui.model.MicroCourseStudent;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.MicroCourseRepository;
import dk.trustworks.invoicewebui.repositories.MicroCourseStudentRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.academy.components.CourseForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SpringComponent
@SpringUI
public class CoursesLayout extends VerticalLayout {

    private final UserService userService;
    private final PhotoRepository photoRepository;
    private final PhotoService photoService;
    private final MicroCourseRepository microCourseRepository;
    private final MicroCourseStudentRepository microCourseStudentRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow coursesRow;

    private CourseForm courseForm;

    @Autowired
    public CoursesLayout(UserService userService, PhotoRepository photoRepository, PhotoService photoService, MicroCourseRepository microCourseRepository, MicroCourseStudentRepository microCourseStudentRepository) {
        this.userService = userService;
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.microCourseRepository = microCourseRepository;
        this.microCourseStudentRepository = microCourseStudentRepository;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public CoursesLayout init() {
        courseForm = new CourseForm(userService, microCourseRepository, microCourseStudentRepository, photoRepository);

        responsiveLayout.removeAllComponents();
        responsiveLayout.addRow(courseForm.getNewCourseButton());
        responsiveLayout.addRow(courseForm.getDialogRow());

        coursesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadCourses();

        return this;
    }

    private void loadCourses() {
        coursesRow.removeAllComponents();
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        for (MicroCourse microCourse : microCourseRepository.findByActiveTrueOrderByCreatedDesc()) {
            BubblesDesign courseDesign = new BubblesDesign();

            MicroCourseStudent microCourseStudent = microCourseStudentRepository.findByMicroCourseAndUseruuid(microCourse, user.getUuid());

            courseDesign.getLblHeading().setValue(microCourse.getName());
            courseDesign.getLblDescription().setValue(microCourse.getDescription());

            courseDesign.getBtnLeave().setVisible(false);
            courseDesign.getBtnEdit().setVisible(false);
            courseDesign.getBtnApply().setVisible(false);
            courseDesign.getBtnJoin().setVisible(false);

            courseDesign.getBtnJoin().setDescription("Sign up for micro course");
            courseDesign.getBtnLeave().setDescription("Withdraw your application for micro course");

            courseDesign.getBtnJoin().setVisible(true);

            courseDesign.getBtnEdit().addClickListener(event -> courseForm.editFormAction(microCourse));

            courseDesign.getBtnJoin().addClickListener(event -> {
                microCourseStudentRepository.save(new MicroCourseStudent(user, microCourse, "ENLISTED"));
                Page.getCurrent().reload();
            });

            courseDesign.getBtnLeave().addClickListener(event -> {
                microCourseStudentRepository.delete(microCourseStudent);
                Page.getCurrent().reload();
            });

            if(microCourseStudent != null && microCourseStudent.getStatus().equals("ENLISTED")) {
                courseDesign.getBtnLeave().setVisible(true);
                courseDesign.getBtnApply().setVisible(false);
                courseDesign.getBtnJoin().setVisible(false);
            }
            if(microCourseStudent != null && microCourseStudent.getStatus().equals("GRADUATED")) {
                courseDesign.getBtnLeave().setVisible(false);
                courseDesign.getBtnApply().setVisible(false);
                courseDesign.getBtnJoin().setVisible(false);
            }
            if(microCourse.getUser().getUuid().equals(user.getUuid())) {
                courseDesign.getBtnEdit().setVisible(true);
                courseDesign.getBtnApply().setVisible(false);
                courseDesign.getBtnJoin().setVisible(false);
                courseDesign.getBtnLeave().setVisible(false);
            }

            courseDesign.getPhotoContainer().setVisible(false);
            courseDesign.getTextContentHolder().setHeight(300, Unit.PIXELS);

            String relatedID = microCourse.getUuid();
            Resource resource = photoService.getRelatedPhoto(relatedID);

            courseDesign.getImgTop().setSource(resource);

            //courseDesign.getGaugeContainer().addComponent(ActivityGauge.getChart(activity));
            courseDesign.getGaugeContainer().setVisible(false);

            coursesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(courseDesign);
        }
    }
}