package dk.trustworks.invoicewebui.web.time.layouts;


import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.TimeManagerViewSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;

import java.time.LocalDate;

@SpringComponent
@SpringUI
public class LessonFramedLayout extends ResponsiveLayout {

    @Autowired private ClientService clientService;
    @Autowired private ProjectService projectService;

    private static final Logger log = LoggerFactory.getLogger(LessonFramedLayout.class);

    public ResponsiveLayout init(String projectuuid) {
        this.removeAllComponents();
        //System.out.println("projectuuid = " + projectuuid);
        Project project = projectService.findOne(projectuuid);
        Client client = clientService.findOne(project.getClientuuid());
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ContainerType.FLUID).withSpacing();
        //responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(projectuuid));

        ResponsiveLayout metadataLayout = new ResponsiveLayout(ContainerType.FLUID);
        ResponsiveRow metadataRow = metadataLayout.addRow();
        metadataRow.setHorizontalSpacing(ResponsiveRow.SpacingSize.NORMAL, true);
        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Client: "));
        metadataRow.addColumn().withDisplayRules(3,3,3,3).withComponent(new MLabel(client.getName()).withStyleName("mystyle"));
        metadataRow.addColumn().withDisplayRules(1,1,1,1);
        DateField startDate = new DateField(null, project.getStartdate());
        //startDate.setStyleName("mystyle");
        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Start date: "));
        metadataRow.addColumn().withDisplayRules(6,6,6,6).withComponent(startDate);

        metadataRow.addColumn().withDisplayRules(12,12,12,12);

        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Project: "));
        metadataRow.addColumn().withDisplayRules(3,3,3,3).withComponent(new MLabel(project.getName()));
        metadataRow.addColumn().withDisplayRules(1,1,1,1);
        DateField endDate = new DateField(null,  project.getEnddate());
        //endDate.setStyleName("mystyle");
        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("End date: "));
        metadataRow.addColumn().withDisplayRules(6,6,6,6).withComponent(endDate);

        metadataRow.addColumn().withDisplayRules(12,12,12,12);

        ComboBox<Object> rolesComboBox = new ComboBox<>();
        //rolesComboBox.setPlaceholder("Select role");
        //rolesComboBox.setStyleName("mystyle");
        rolesComboBox.setSizeFull();
        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Role: "));
        metadataRow.addColumn().withDisplayRules(3,3,3,3).withComponent(rolesComboBox);
        metadataRow.addColumn().withDisplayRules(1,1,1,1);
        metadataRow.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Comment: "));
        metadataRow.addColumn().withDisplayRules(5,5,5,5).withComponent(new MTextField().withFullWidth());
        metadataRow.addColumn().withDisplayRules(1,1,1,1);
        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(metadataLayout));

        ResponsiveLayout slidersResponsiveLayout = new ResponsiveLayout(ContainerType.FLUID);

        String[] leftSideArray = {"Forankring af viden", "Clock building", "Passion", "Frit valg", "Kundefocus", "Good people"};
        String[] rightSideArray = {"Ny viden", "Time telling", "Fornuft", "Kontrol", "TW Fokus", "Exceptionel person"};

        //String[] leftSideArray = {"Forankring af viden", "Clock building", "Passion", "Frit valg", "Kundefocus", "Good people", "Ny viden", "Time telling", "Fornuft", "Kontrol", "TW Fokus", "Exceptionel person"};

        ResponsiveRow slidersRow = slidersResponsiveLayout.addRow();
        slidersRow.setHorizontalSpacing(ResponsiveRow.SpacingSize.NORMAL, true);
        slidersRow.addColumn().withDisplayRules(12,12,12,12);
        for (int i = 0; i < leftSideArray.length; i++) {
            Slider slider1 = new Slider(0,10,0);
            slider1.setSizeFull();
            slider1.setValue(5.0);
            slidersRow.addColumn().withDisplayRules(3,3,3,3).withComponent(new MLabel(leftSideArray[i]).withDescription("Her er en beskrivelse af denne værdi"), ResponsiveColumn.ColumnComponentAlignment.RIGHT);
            slidersRow.addColumn().withDisplayRules(6,6,6,6).withComponent(slider1);
            slidersRow.addColumn().withDisplayRules(3,3,3,3).withComponent(new MLabel(rightSideArray[i]).withDescription("Her er en beskrivelse af den anden værdi"));
            slidersRow.addColumn().withDisplayRules(12,12,12,12);
        }

        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(slidersResponsiveLayout));

        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,12,12).withComponent(new BoxImpl().instance(new MButton("Submit").withListener(event -> {
            UI.getCurrent().getNavigator().navigateTo(TimeManagerViewSecond.VIEW_NAME);
        })), ResponsiveColumn.ColumnComponentAlignment.RIGHT);

        this.addRow().addColumn()
                .withDisplayRules(12, 12, 10, 10)
                .withOffset(DisplaySize.LG, 1)
                .withOffset(DisplaySize.MD, 1)
                .withComponent(responsiveLayout);
        return this;
    }

}

