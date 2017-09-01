package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import org.joda.time.DateTime;

/**
 * Created by hans on 11/08/2017.
 */
public class BirthdayCardImpl extends BirthdayCardDesign implements Box {

    private int priority;
    private int boxWidth;
    private String name;

    public BirthdayCardImpl(int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        DateTime now = DateTime.now();
        String season;
        int month_day = now.getMonthOfYear() * 100 + now.getDayOfMonth();
        if (month_day <= 315) {
            season = "winter.jpg";
        }
        else if (month_day <= 615) {
            season = "spring.jpg";
        }
        else if (month_day <= 915) {
            season = "summer.jpg";
        }
        else if (month_day <= 1215) {
            season = "fall.jpg";
        }
        else {
            season = "winter.jpg";
        }

        getImgTop().setSource(new ThemeResource("images/cards/"+season));
        getImgTop().setSizeFull();

        String[] text = {"Tommy turns 37", "Going to [wouldn't you like to know]", "After work beer"};
        String[] dates = {"1.9.2017", "27.9.2017", "11.12.2017"};

        getEventGrid().setRows(text.length);
        getEventGrid().setColumnExpandRatio(1, 1.0f);

        for (int i = 0; i < text.length; i++) {
            Label lblDate = new Label(dates[i]);
            Label lblText = new Label(text[i]);
            getEventGrid().addComponent(lblDate, 0, i);
            getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
            getEventGrid().addComponent(lblText, 1, i);
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }

}
