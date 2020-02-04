package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.google.common.hash.Hashing;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.News;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.EventType;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.services.UserService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.crudui.crud.CrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.GridLayoutCrudFormFactory;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 11/08/2017.
 */
public class NewsImpl extends NewsDesign implements Box {

    private static final Logger log = LoggerFactory.getLogger(NewsImpl.class);

    private int priority;
    private int boxWidth;
    private String name;

    public NewsImpl(UserService userService, NewsRepository newsRepository, int priority, int boxWidth, String name) {
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

        for (User user : userService.findCurrentlyEmployedUsers()) {
            LocalDate birthday = new LocalDate(user.getBirthday().getYear(), user.getBirthday().getMonthValue(), user.getBirthday().getDayOfMonth());
            Date nextBirthday = birthday.withYear(LocalDate.now().getYear()).toDate();
            if(!isBetweenInclusive(LocalDate.now(), LocalDate.now().plusWeeks(2), LocalDate.fromDateFields(nextBirthday))) continue;
            News news = new News(user.getFirstname() + "'s Birthday", nextBirthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), EventType.BIRTHDAY.name(), "", user);
            newsList.add(news);
        }

        for (News news : newsRepository.findAll()) {
            newsList.add(news);
            if(news.getNewstype().equalsIgnoreCase("project")) news.setNewsdate(java.time.LocalDate.now().plusMonths(12));
        }

        getEventGrid().setRows(newsList.size()+1);
        getEventGrid().setColumnExpandRatio(1, 1.0f);
        int i = 0;
        for (News newsItem : newsList.stream().sorted(Comparator.comparing(News::getNewsdate)).collect(Collectors.toList())) {
            Label lblDate;
            if(newsItem.getNewstype().equalsIgnoreCase("project")) {
                lblDate = new Label("");
            } else {
                lblDate = new Label(newsItem.getNewsdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            MHorizontalLayout textLayout = new MHorizontalLayout().add(new MLabel(newsItem.getDescription()).withWidth("100%")).withWidth("100%");
            textLayout.addLayoutClickListener(e -> UI.getCurrent().getNavigator().navigateTo(newsItem.getLink()));
            if (!newsItem.getNewstype().equalsIgnoreCase("project")) {
                getEventGrid().addComponent(lblDate, 0, i);
                getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
                getEventGrid().addComponent(textLayout, 1, i);
            }

            i++;
        }

        getBtnEditNews().addClickListener(clickEvent -> {
            GridCrud<News> crud = new GridCrud<>(News.class);
            Window window = new Window("Edit news", crud);
            window.setModal(true);
            window.setWidth(500, Unit.PIXELS);
            window.setHeight(500, Unit.PIXELS);
            UI.getCurrent().addWindow(window);

            crud.setCrudListener(new CrudListener<News>() {
                @Override
                public Collection<News> findAll() {
                    return newsRepository.findByNewstypeIn("user", "new_employee", "anniversary");
                }

                @Override
                public News add(News news) {
                    news.setUuid(UUID.randomUUID().toString());
                    news.setNewstype("user");
                    news.setSha512(Hashing.sha512().hashString(news.getUuid()+UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString());
                    news.setLink("");
                    return newsRepository.save(news);
                }

                @Override
                public News update(News news) {
                    return newsRepository.save(news);
                }

                @Override
                public void delete(News news) {
                    newsRepository.delete(news);
                }
            });

            GridLayoutCrudFormFactory<News> formFactory = new GridLayoutCrudFormFactory<>(News.class, 3, 10);
            crud.setCrudFormFactory(formFactory);
            crud.refreshGrid();

            crud.getGrid().getColumn("uuid").setHidden(true);
            crud.getGrid().getColumn("sha512").setHidden(true);
            crud.getGrid().getColumn("link").setHidden(true);
            crud.getGrid().getColumn("newstype").setHidden(true);
            crud.getGrid().getColumn("description").setExpandRatio(1);

            formFactory.setVisibleProperties("newsdate", "description");
            formFactory.setFieldCreationListener("newsdate", field -> ((DateField) field).setDateFormat("yyyy-MM-dd"));

            formFactory.setFieldProvider("description", () -> {
                TextArea textArea = new TextArea();
                textArea.setSizeFull();
                return textArea;
            });

        });

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
