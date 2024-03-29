package dk.trustworks.invoicewebui.web.academy.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.CkoCourse;
import dk.trustworks.invoicewebui.model.CkoCourseStudent;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.MicroCourseRepository;
import dk.trustworks.invoicewebui.repositories.MicroCourseStudentRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.academy.components.CourseForm;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.dashboard.cards.PhotosCardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.label.MLabel;

@SpringComponent
@SpringUI
public class BasicSkillsLayout extends VerticalLayout {

    private final UserService userService;
    private final PhotoService photoService;
    private final MicroCourseRepository microCourseRepository;
    private final MicroCourseStudentRepository microCourseStudentRepository;

    private ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
    private ResponsiveRow coursesRow;

    private CourseForm courseForm;

    @Autowired
    public BasicSkillsLayout(UserService userService, PhotoService photoService, MicroCourseRepository microCourseRepository, MicroCourseStudentRepository microCourseStudentRepository) {
        this.userService = userService;
        this.photoService = photoService;
        this.microCourseRepository = microCourseRepository;
        this.microCourseStudentRepository = microCourseStudentRepository;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public BasicSkillsLayout init() {
        courseForm = new CourseForm("basic", userService, microCourseRepository, photoService);

        responsiveLayout.removeAllComponents();
        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new PhotosCardImpl().loadResourcePhoto("images/banners/trustworks-academy-basic-skills.png").withFullWidth());
        //responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(new Label("<p>Her kan jeg læse om de obligatoriske kurser</p><p>AC1: Når som Trustworker står på siden 'Basis TW Info' under Knowledge har jeg som Trustworker mulighed for at læse om de obligatoriske TW 'kurser'</p>")));
        responsiveLayout.addRow(courseForm.getNewCourseButton());
        responsiveLayout.addRow(courseForm.getDialogRow());

        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(
                new BoxImpl().instance(
                        new MLabel("Basic skills er både til dig der er helt ny i konsulentverdenen, " +
                                "men i særdeleshed til dig der tillidsfuldt skal evne at samarbejde og håndtere " +
                                "relationer, derfor prioriterer vi alle konsulenter deltager i Basic Skills")
                                .withWidth(80, Unit.PERCENTAGE).withStyleName("large").withStyleName("center-label")));

        coursesRow = responsiveLayout.addRow();
        this.addComponent(responsiveLayout);

        loadCourses();

        return this;
    }

    private void loadCourses() {
        coursesRow.removeAllComponents();
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        for (CkoCourse ckoCourse : microCourseRepository.findByTypeAndActiveTrueOrderByNameAsc("basic")) {
            BubblesDesign courseDesign = new BubblesDesign();

            CkoCourseStudent ckoCourseStudent = microCourseStudentRepository.findByCkoCourseAndUseruuid(ckoCourse, user.getUuid());

            courseDesign.getLblHeading().setHeight(50, Unit.PIXELS);
            courseDesign.getLblHeading().setValue(ckoCourse.getName());
            courseDesign.getLblDescription().setValue(ckoCourse.getDescription());

            courseDesign.getBtnLeave().setVisible(false);
            courseDesign.getBtnEdit().setVisible(false);
            courseDesign.getBtnApply().setVisible(true);
            courseDesign.getBtnJoin().setVisible(false);
            courseDesign.getBtnApply().setIcon(MaterialIcons.SCHOOL);

            courseDesign.getBtnJoin().setDescription("Sign up for course");
            courseDesign.getBtnLeave().setDescription("Withdraw your application for course");

            courseDesign.getBtnApply().setStyleName("grey-icon flat tiny");
            courseDesign.getBtnApply().setDescription("I haven't yet graduated from this course! (Click to change status)");

            courseDesign.getBtnJoin().setVisible(true);

            courseDesign.getBtnEdit().addClickListener(event -> courseForm.editFormAction(ckoCourse));

            courseDesign.getBtnJoin().addClickListener(event -> {
                microCourseStudentRepository.save(new CkoCourseStudent(user, ckoCourse, "ENLISTED"));
                Page.getCurrent().reload();
            });

            courseDesign.getBtnLeave().addClickListener(event -> {
                microCourseStudentRepository.delete(ckoCourseStudent);
                Page.getCurrent().reload();
            });

            courseDesign.getBtnApply().addClickListener(event -> {
                if(ckoCourseStudent == null) microCourseStudentRepository.save(new CkoCourseStudent(user, ckoCourse, "GRADUATED"));
                else if(ckoCourseStudent.getStatus().equals("ENLISTED")) {
                    ckoCourseStudent.setStatus("GRADUATED");
                    microCourseStudentRepository.save(ckoCourseStudent);
                } else microCourseStudentRepository.delete(ckoCourseStudent);
                Page.getCurrent().reload();
            });

            if(ckoCourseStudent != null && ckoCourseStudent.getStatus().equals("ENLISTED")) {
                courseDesign.getBtnLeave().setVisible(true);
                courseDesign.getBtnJoin().setVisible(false);
            }
            if(ckoCourseStudent != null && ckoCourseStudent.getStatus().equals("GRADUATED")) {
                courseDesign.getBtnApply().setDescription("Yay - I graduated from this course! (Click to change status)");
                courseDesign.getBtnApply().setStyleName("flat tiny");
                courseDesign.getBtnLeave().setVisible(false);
                courseDesign.getBtnJoin().setVisible(false);
            }
            if(ckoCourse.getUser().getUuid().equals(user.getUuid()) || user.getUsername().equals("marie.myssing")) {
                courseDesign.getBtnEdit().setVisible(true);
                courseDesign.getBtnJoin().setVisible(false);
                courseDesign.getBtnLeave().setVisible(false);
            }

            courseDesign.getPhotosContentHolder().setVisible(false);
            courseDesign.getTextContentHolder().setHeight(300, Unit.PIXELS);

            String relatedID = ckoCourse.getUuid();
            Resource resource = photoService.getRelatedPhotoResource(relatedID);

            courseDesign.getImgTop().setSource(resource);
            if(ckoCourse.getUser().getUuid().equals(user.getUuid()) || user.getUsername().equals("marie.myssing")) {
                courseDesign.getImgTop().addClickListener(event -> courseForm.editPhotoAction(ckoCourse));
            }

            //courseDesign.getGaugeContainer().addComponent(ActivityGauge.getChart(activity));
            courseDesign.getGaugeContainer().setVisible(false);

            coursesRow.addColumn().withDisplayRules(12, 12, 6,4).withComponent(courseDesign);
        }
    }
}