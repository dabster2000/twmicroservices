package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.EventType;
import dk.trustworks.invoicewebui.model.TrustworksEvent;
import dk.trustworks.invoicewebui.repositories.TrustworksEventRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by hans on 11/08/2017.
 */
public class BirthdayCardImpl extends BirthdayCardDesign implements Box {


    private int priority;
    private int boxWidth;
    private String name;

    public BirthdayCardImpl(TrustworksEventRepository trustworksEventRepository, int priority, int boxWidth, String name) {
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

        List<TrustworksEvent> trustworksEvents = trustworksEventRepository.findByEventdateBetweenOrEventtype(LocalDate.now().toDate(), LocalDate.now().plusMonths(1).toDate(), EventType.BIRTHDAY);
        trustworksEvents.sort(Comparator.comparing(TrustworksEvent::getEventdate).reversed());

        getEventGrid().setRows(trustworksEvents.size());
        getEventGrid().setColumnExpandRatio(1, 1.0f);

        this.setVisible(false);
        int i = 0;
        for (TrustworksEvent trustworksEvent : trustworksEvents) {
            Date eventDate = trustworksEvent.getEventdate();
            if(trustworksEvent.getEventtype().equals(EventType.BIRTHDAY)) eventDate = LocalDate.fromDateFields(eventDate).withYear(LocalDate.now().getYear()).toDate();
            if(!isBetweenInclusive(LocalDate.now(), LocalDate.now().plusWeeks(2), LocalDate.fromDateFields(eventDate))) continue;
            if(new SimpleDateFormat("yyyy-MM-dd").format(eventDate)
                    .equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                    && trustworksEvent.getEventtype().equals(EventType.BIRTHDAY)) {
                season = "birthday"+(new Random().nextInt(5 - 1 + 1) + 1)+".jpg";
                this.boxWidth = 12;
                this.priority = 0;
                getLblBirthdayGreeting().setVisible(true);
                getLblBirthdayGreeting().setValue("Happy Birthday, "+trustworksEvent.getName());
                getEventGrid().setVisible(false);
                getLblHeading().setVisible(false);
                getPanelContentHolder().setHeightUndefined();
                this.setVisible(true);
                break;
            } else {
                Label lblDate = new Label(new SimpleDateFormat("yyyy-MM-dd").format(eventDate));
                Label lblText = new Label(trustworksEvent.getName()+(trustworksEvent.getEventtype().equals(EventType.BIRTHDAY)?"'s Birthday":""));
                getEventGrid().addComponent(lblDate, 0, i);
                getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
                getEventGrid().addComponent(lblText, 1, i);
                this.setVisible(true);
                i++;
            }
        }
        getEventGrid().setRows(++i);

        getImgTop().setSource(new ThemeResource("images/cards/"+season));
        getImgTop().setSizeFull();
    }

    boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
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
