package dk.trustworks.invoicewebui.web.time.layouts;


import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.network.rest.CultureRestService;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.TimeManagerViewSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.time.LocalDate;
import java.util.*;

@SpringComponent
@SpringUI
public class LessonFramedLayout extends ResponsiveLayout {

    @Autowired private ClientService clientService;
    @Autowired private ProjectService projectService;
    @Autowired private CultureRestService cultureRestService;

    private static final Logger log = LoggerFactory.getLogger(LessonFramedLayout.class);

    public void init(String projectuuid, String reason) {
        this.removeAllComponents();

        Project project = projectService.findOne(projectuuid);
        Client client = clientService.findOne(project.getClientuuid());
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ContainerType.FLUID).withSpacing();

        //ResponsiveLayout introLayout = new ResponsiveLayout(ContainerType.FLUID);
        ResponsiveLayout metadataLayout = new ResponsiveLayout(ContainerType.FLUID);
        ResponsiveRow introRow = metadataLayout.addRow();
        introRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel("Lesson Framed").withStyleName("h4", "center-label").withFullWidth());
        introRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel());
        String content = "";
        switch (reason) {
            case "projectstart":
                content ="So you just started on the project '" + project.getName() + "' at " + client.getName() + ". Please fill out this Lesson Framed form to evaluate your expectations for this project.";
                break;
            case "3months":
                content ="You have worked on the project '" + project.getName() + "' at " + client.getName() + " for more than 3 months now. Please fill out this Lesson Framed form to evaluate the project at this time.";
                break;
            case "projectend":
                content = "It seems your project '" + project.getName() + "' at " + client.getName() + " ended last month. Please fill out this Lesson Framed form to evaluate the project.";
                break;
        }
        introRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel(content).withStyleName("center-label").withWidth(80, Unit.PERCENTAGE), ResponsiveColumn.ColumnComponentAlignment.CENTER);
        introRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new MLabel());
        //responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(metadataLayout));

        Binder<Lesson> binder = new Binder<>();
        Lesson lesson = new Lesson();
        lesson.setProjectuuid(projectuuid);
        lesson.setUseruuid(userSession.getUser().getUuid());
        lesson.setUuid(UUID.randomUUID().toString());
        lesson.setRegistered(LocalDate.now());
        binder.setBean(lesson);


        ResponsiveRow metadataRow = metadataLayout.addRow();
        metadataRow.setHorizontalSpacing(ResponsiveRow.SpacingSize.NORMAL, true);

        metadataRow.addColumn().withDisplayRules(0,0,1,1);
        //metadataRow.addColumn().withDisplayRules(4,4,1,1).withComponent(new MLabel("Client: ").withStyleName("bold"));
        metadataRow.addColumn().withDisplayRules(8,8,4,4).withComponent(new MLabel(client.getName()).withCaption("Client:"));
        metadataRow.addColumn().withDisplayRules(0,0,12,12);
        metadataRow.addColumn().withDisplayRules(0,0,1,1);
        //metadataRow.addColumn().withDisplayRules(4,4,1,1).withComponent(new MLabel("Project: ").withStyleName("bold"));
        metadataRow.addColumn().withDisplayRules(8,8,4,4).withComponent(new MLabel(project.getName()).withCaption("Project:"));
        metadataRow.addColumn().withDisplayRules(0,0,12,12);

        metadataRow.addColumn().withDisplayRules(12,12,12,12);

        metadataRow.addColumn().withDisplayRules(0,0,1,1);

        DateField startDate = new DateField(null, project.getStartdate());
        startDate.setStyleName("floating");
        startDate.setCaption("Start date:");
        startDate.setSizeFull();
        binder.forField(startDate).asRequired("You must select a start date!").bind(Lesson::getStartdate, Lesson::setStartdate);
        //metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Start date: ").withStyleName("bold"));
        metadataRow.addColumn().withDisplayRules(12,12,3,3).withComponent(startDate);

        DateField endDate = new DateField(null,  project.getEnddate());
        endDate.setStyleName("floating");
        endDate.setCaption("End date:");
        endDate.setSizeFull();
        binder.forField(endDate).bind(Lesson::getEnddate, Lesson::setEnddate);
        //metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("End date: ").withStyleName("bold"));
        metadataRow.addColumn().withDisplayRules(12,12,3,3).withComponent(endDate);

        ComboBox<LessonRole> rolesComboBox = new ComboBox<>();
        rolesComboBox.setPlaceholder("Select role");
        rolesComboBox.setSizeFull();
        rolesComboBox.setItems(cultureRestService.findAllRoles());
        rolesComboBox.setItemCaptionGenerator(LessonRole::getName);
        rolesComboBox.setCaption("Role:");
        binder.forField(rolesComboBox).asRequired("You must select a role!").bind(Lesson::getRole, Lesson::setRole);
        //metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Role: ").withStyleName("bold"));
        metadataRow.addColumn().withDisplayRules(12,12,4,4).withComponent(rolesComboBox);

        metadataRow.addColumn().withDisplayRules(0,0,1,1);

        metadataRow.addColumn().withDisplayRules(12,12,12,12);

        //metadataRow.addColumn().withDisplayRules(1,1,1,1);
        //metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Comment: ").withStyleName("bold"));
        TextField comment = new MTextField().withCaption("Comment:").withFullWidth();
        binder.forField(comment).bind(Lesson::getNote, Lesson::setNote);
        metadataRow.addColumn().withDisplayRules(0,0,1,1);
        metadataRow.addColumn().withDisplayRules(12,12,10,10).withComponent(comment);
        metadataRow.addColumn().withDisplayRules(0,0,1,1);
        //metadataRow.addColumn().withDisplayRules(1,1,1,1);
        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(metadataLayout));

        ResponsiveLayout slidersResponsiveLayout = new ResponsiveLayout(ContainerType.FLUID);

        List<PerformanceGroups> performanceGroups = cultureRestService.findActivePerformanceGroups();
        ResponsiveRow slidersRow = slidersResponsiveLayout.addRow();
        slidersRow.setHorizontalSpacing(ResponsiveRow.SpacingSize.NORMAL, true);
        slidersRow.addColumn().withDisplayRules(12,12,12,12);
        Map<PerformanceKey, Slider> resultMap = new HashMap<>();
        for (PerformanceGroups performanceGroup : performanceGroups) {
            slidersRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new MLabel(performanceGroup.getName()).withStyleName("h4", "center-label").withFullWidth());
            for (PerformanceKey performanceKey : performanceGroup.getPerformanceKeys()) {
                Slider slider = new Slider(0,10,0);
                slider.setSizeFull();
                slider.setValue(0.0);
                slidersRow.addColumn().withDisplayRules(3,3,3,3).withComponent(
                        new MHorizontalLayout(new MLabel(performanceKey.getName()).withUndefinedSize()).withDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
                        , ResponsiveColumn.ColumnComponentAlignment.RIGHT
                );
                slidersRow.addColumn().withDisplayRules(6,6,6,6).withComponent(slider);
                slidersRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MButton(MaterialIcons.HELP).withStyleName("borderless").withListener(event -> Notification.show(performanceKey.getName(), performanceKey.getDescription(), Notification.Type.HUMANIZED_MESSAGE)));
                slidersRow.addColumn().withDisplayRules(12,12,12,12);
                resultMap.put(performanceKey, slider);
            }
        }

        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(slidersResponsiveLayout));

        ResponsiveRow buttonRow = slidersResponsiveLayout.addRow();
        buttonRow.addColumn().withDisplayRules(12,12,6,6)
                .withComponent(new MButton("Submit").withStyleName("friendly").withFullWidth().withListener(event -> {
                    if(binder.validate().isOk()) {
                        for (PerformanceKey performanceKey : resultMap.keySet()) {
                            Slider slider = resultMap.get(performanceKey);
                            PerformanceResult performanceResult = new PerformanceResult();
                            performanceResult.setResult(slider.getValue().intValue());
                            performanceResult.setPk_uuid(performanceKey.getUuid());
                            performanceResult.setUuid(UUID.randomUUID().toString());
                            lesson.addPerformanceResult(performanceResult);
                        }

                        cultureRestService.save(lesson);
                        UI.getCurrent().getNavigator().navigateTo(TimeManagerViewSecond.VIEW_NAME);
                    }
                }));
        System.out.println("7");
        buttonRow.addColumn().withDisplayRules(12,12,6,6)
                .withComponent(new MButton("Delay").withStyleName("danger")
                        .withEnabled(!reason.equals("projectend"))
                        .withFullWidth().withListener(event -> UI.getCurrent().getNavigator().navigateTo(TimeManagerViewSecond.VIEW_NAME)));

        System.out.println("8");
        this.addRow().addColumn()
                .withDisplayRules(12, 12, 10, 10)
                .withOffset(DisplaySize.LG, 1)
                .withOffset(DisplaySize.MD, 1)
                .withComponent(responsiveLayout);
        System.out.println("9");
    }
}

