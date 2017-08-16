package dk.trustworks.invoicewebui.web.time;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import org.joda.time.LocalDate;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@SpringView(name = TimeManagerViewImpl.VIEW_NAME)
public class TimeManagerViewImpl extends TimeManagerViewDesign implements View {

    public static final String VIEW_NAME = "time";

    private LocalDate currentDate = LocalDate.now();

    @PostConstruct
    void init() {
        setDateFields();

        getBtnWeekNumberDecr().addClickListener(event -> {
            currentDate = currentDate.minusWeeks(1);
            setDateFields();
        });

        getBtnWeekNumberIncr().addClickListener(event -> {
            currentDate = currentDate.plusWeeks(1);
            setDateFields();
        });

        getBtnYearDecr().addClickListener(event -> {
            currentDate = currentDate.minusYears(1);
            setDateFields();
        });

        getBtnYearIncr().addClickListener(event -> {
            currentDate = currentDate.plusYears(1);
            setDateFields();
        });
    }

    private void setDateFields() {
        getTxtWeekNumber().setValue(currentDate.getWeekOfWeekyear()+"");
        getTxtYear().setValue(currentDate.getYear()+"");
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }
}
