package dk.trustworks.invoicewebui.web.time.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@SpringUI
public class TimeCardImpl extends TimeCardDesign {

    private static final Logger log = LoggerFactory.getLogger(TimeManagerImpl.class);

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ProjectRepository projectRepository;

    private LocalDate currentDate = LocalDate.now().withDayOfWeek(1);

    private Label lblCurrentDate;

    private ComboBox<User> selActiveUser;
    private TextField txtWeekNumber;
    private TextField txtYear;

    public TimeCardImpl() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        getContainer().addComponent(responsiveLayout);

        ResponsiveRow controlsRow = responsiveLayout.addRow();
        controlsRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(getLblCurrentDate());
    }


    public TimeCardDesign init() {
        return this;
    }

    private void setDateFields() {
        log.info("TimeManagerImpl.setDateFields");
        getTxtWeekNumber().setValue(currentDate.getWeekOfWeekyear() + "");
        log.info("Text Weeknumber = " + currentDate.getWeekOfWeekyear());
        getTxtYear().setValue(currentDate.getYear() + "");
        log.info("Text Year = " + currentDate.getYear());
        getLblCurrentDate().setValue(currentDate.toString("dd. MMM yyyy") + " - " + currentDate.withDayOfWeek(7).toString("dd. MMM yyyy"));
        log.info("Top Dates = " + (currentDate.toString("dd. MMM yyyy") + " - " + currentDate.withDayOfWeek(7).toString("dd. MMM yyyy")));
    }

    private Component createDateButtons() {

        Button btnWeekNumberDecr = new Button();
        Button btnWeekNumberIncr = new Button();
        Button btnYearNumberDecr = new Button();
        Button btnYearNumberIncr = new Button();


        btnWeekNumberDecr.addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            log.info("currentDate.minusWeeks(1) = " + currentDate);
            setDateFields();
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        btnWeekNumberIncr.addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            log.info("currentDate.plusWeeks(1) = " + currentDate);
            setDateFields();
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        btnYearNumberDecr.addClickListener(event -> {
            currentDate = currentDate.minusYears(1);
            log.info("currentDate.minusYears(1) = " + currentDate);
            setDateFields();
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });

        btnYearNumberIncr.addClickListener(event -> {
            currentDate = currentDate.plusYears(1);
            log.info("currentDate.plusYears(1) = " + currentDate);
            setDateFields();
            //updateGrid(getSelActiveUser().getSelectedItem().get());
        });
        return null;
    }

    public Label getLblCurrentDate() {
        return lblCurrentDate;
    }

    public ComboBox<User> getSelActiveUser() {
        return selActiveUser;
    }

    public TextField getTxtWeekNumber() {
        return txtWeekNumber;
    }

    public TextField getTxtYear() {
        return txtYear;
    }
}
