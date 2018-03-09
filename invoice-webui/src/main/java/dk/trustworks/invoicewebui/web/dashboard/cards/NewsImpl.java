package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.EventType;
import dk.trustworks.invoicewebui.model.News;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by hans on 11/08/2017.
 */
public class NewsImpl extends NewsDesign implements Box {

    private static final Logger log = LoggerFactory.getLogger(NewsImpl.class);

    private int priority;
    private int boxWidth;
    private String name;

    public NewsImpl(UserRepository userRepository, NewsRepository newsRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        DateTime now = DateTime.now();
        String season;
        int month_day = now.getMonthOfYear() * 100 + now.getDayOfMonth();
        if (month_day <= 315) {
            season = "winter.gif";
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
            season = "winter.gif";
        }


        Set<News> newsList = new TreeSet<>(Comparator.comparing(News::getNewstype).thenComparing(News::getNewsdate).thenComparing(News::getSha512));

        boolean isBirthdayToday = false;
        for (User user : userRepository.findByActiveTrue()) {
            Date birthday = user.getBirthday();
            Date nextBirthday = LocalDate.fromDateFields(birthday).withYear(LocalDate.now().getYear()).toDate();
            if(!isBetweenInclusive(LocalDate.now(), LocalDate.now().plusWeeks(2), LocalDate.fromDateFields(nextBirthday))) continue;
            News news = new News(user.getFirstname() + "'s Birthday", nextBirthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), EventType.BIRTHDAY.name(), "", user);
            log.info("Add news "+news, news);
            newsList.add(news);
            log.info("newsList.size() = "+newsList.size(), newsList);

            if(new SimpleDateFormat("yyyy-MM-dd").format(nextBirthday)
                    .equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                /*
                season = "birthday"+(new Random().nextInt(5 - 1 + 1) + 1)+".jpg";
                this.boxWidth = 12;
                this.priority = 0;
                getLblBirthdayGreeting().setVisible(true);
                getLblBirthdayGreeting().setValue("Happy Birthday, "+user.getFirstname());
                getEventGrid().setVisible(false);
                getLblHeading().setVisible(false);
                getPanelContentHolder().setHeightUndefined();
                isBirthdayToday = true;
                //break;
                */
            }
        }

        for (News news : newsRepository.findAll()) {
            newsList.add(news);
            if(news.getNewstype().equalsIgnoreCase("project")) news.setNewsdate(java.time.LocalDate.now().plusMonths(12));
            log.info("Add news "+news, news);
            log.info("newsList.size() = "+newsList.size(), newsList);
        }

        log.info("newsList.size() = "+newsList.size(), newsList);
        getEventGrid().setRows(newsList.size());
        getEventGrid().setColumnExpandRatio(1, 1.0f);
        int i = 0;
        for (News newsItem : newsList) {
            Label lblDate;
            if(newsItem.getNewstype().equalsIgnoreCase("project")) {
                lblDate = new Label("");
            } else {
                lblDate = new Label(newsItem.getNewsdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            MHorizontalLayout textLayout = new MHorizontalLayout().add(new MLabel(newsItem.getDescription()).withWidth("100%")).withWidth("100%");
            textLayout.addLayoutClickListener(e -> UI.getCurrent().getNavigator().navigateTo(newsItem.getLink()));
            getEventGrid().addComponent(lblDate, 0, i);
            getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
            getEventGrid().addComponent(textLayout, 1, i);
            i++;
        }


        /*
        List<TrustworksEvent> trustworksEvents = trustworksEventRepository.findByEventdateBetweenOrEventtype(LocalDate.now().toDate(), LocalDate.now().plusMonths(1).toDate(), EventType.BIRTHDAY);
        trustworksEvents.sort(Comparator.comparing(TrustworksEvent::getEventdate).reversed());
        */

        /*
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
                birthday = true;
                break;
            } else {
                Label lblDate = new Label(new SimpleDateFormat("yyyy-MM-dd").format(eventDate));
                Label lblText = new Label(trustworksEvent.getName()+(trustworksEvent.getEventtype().equals(EventType.BIRTHDAY)?"'s Birthday":""));
                getEventGrid().addComponent(lblDate, 0, i);
                getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
                getEventGrid().addComponent(lblText, 1, i);
                i++;
            }
            if(i>=getEventGrid().getRows()) break;
        }
        */

/*
        if(!birthday) {
            List<News> newsList = newsRepository.findTop10ByOrderByNewsdateDesc();

            if (newsList.size() > 0) {
                getEventGrid().setRows((newsList.size() < 6) ? newsList.size() : 5);
            }
            getEventGrid().setColumnExpandRatio(1, 1.0f);

            //i = 0;
            for (News newsItem : newsList) {
                //if (i > 4) break;
                //if(getEventGrid().getRows() < i) break;
                Label lblDate = new Label(newsItem.getNewsdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                MHorizontalLayout textLayout = new MHorizontalLayout().add(new MLabel(newsItem.getDescription()).withWidth("100%")).withWidth("100%");
                textLayout.addLayoutClickListener(e -> UI.getCurrent().getNavigator().navigateTo(newsItem.getLink()));
                getEventGrid().addComponent(lblDate, 0, i);
                getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
                getEventGrid().addComponent(textLayout, 1, i);
                //i++;
            }
        //}*/

        getImgTop().setSource(new ThemeResource("images/cards/"+season));
        getImgTop().setSizeFull();
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

    boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

}
