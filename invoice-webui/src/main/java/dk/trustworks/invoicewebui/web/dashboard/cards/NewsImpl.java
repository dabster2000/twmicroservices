package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.web.model.NewsItem;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by hans on 11/08/2017.
 */
public class NewsImpl extends NewsDesign implements Box {


    private int priority;
    private int boxWidth;
    private String name;

    public NewsImpl(UserRepository userRepository, ProjectRepository projectRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;

        getEventGrid().setColumnExpandRatio(1, 1.0f);

        List<NewsItem> newsItems = new ArrayList<>();
        List<Project> projects = projectRepository.findAllByActiveTrueOrderByNameAsc();

        for (User user : userRepository.findByActiveTrue()) {
            List<UserStatus> statuses = user.getStatuses();
            statuses.sort(Comparator.comparing(UserStatus::getStatusdate));
            UserStatus firstStatus = statuses.get(0);
            if(firstStatus.getStatusdate().isAfter(LocalDate.now().minusMonths(1))) {
                newsItems.add(new NewsItem(firstStatus.getStatusdate(),
                        "A huge welcome to "+user.getFirstname()+" "+user.getLastname()+
                                " who just joined Trustworks!"));
                continue;
            }
            LocalDate dateWithCurrentYear = firstStatus.getStatusdate().withYear(LocalDate.now().getYear());
            if(dateWithCurrentYear.isAfter(LocalDate.now()) &&
                    dateWithCurrentYear.isBefore(LocalDate.now().plusMonths(1))) {
                newsItems.add(new NewsItem(dateWithCurrentYear,
                        user.getFirstname()+" "+user.getLastname()+
                                ", You are... terrifically tireless, exceptionally, excellent, abundantly appreciated and..." +
                                "magnificient beyond words! So glad you're a part of our team! Happy "+
                                (LocalDate.now().getYear()-firstStatus.getStatusdate().getYear())+
                                ". year anniversary."));
                continue;
            }
        }

        for (Project project : projects) {
            if(project.getStartdate().isAfter(LocalDate.now().minusMonths(1))) {
                String consultants = "";
                for (Task task : project.getTasks()) {
                    for (Taskworkerconstraint taskworkerconstraint : task.getTaskworkerconstraint()) {
                        if(taskworkerconstraint.getPrice() >= 1.0) continue;
                        consultants += taskworkerconstraint.getUser().getFirstname() + " " + taskworkerconstraint.getUser().getLastname() + ", ";
                    }
                }

                newsItems.add(new NewsItem(project.getStartdate(), "We have started an exciting new project recently! " +
                        "Its called '"+project.getName()+"' and its for our client "+project.getClient().getName()+". " +
                        "From what have been announced, "+consultants+" have been assigned as consultants."));
            }
            if(project.getEnddate().isBefore(LocalDate.now().plusMonths(1)) && project.getEnddate().isAfter(LocalDate.now())) {
                String consultants = "";
                for (Task task : project.getTasks()) {
                    for (Taskworkerconstraint taskworkerconstraint : task.getTaskworkerconstraint()) {
                        if(taskworkerconstraint.getPrice() >= 1.0) continue;
                        consultants += taskworkerconstraint.getUser().getFirstname() + " " + taskworkerconstraint.getUser().getLastname() + ", ";
                    }
                }
                newsItems.add(new NewsItem(project.getEnddate(), "The project '"+project.getName()+"' " +
                        "for our client "+project.getClient().getName()+" is ending soon. " +
                        "Your colleagues "+consultants+" will be available for other tasks."));
            }
        }

        System.out.println("newsItems = " + newsItems.size());
        for (NewsItem newsItem : newsItems) {
            System.out.println("newsItem = " + newsItem);
        }


        getEventGrid().setRows((newsItems.size()<6)?newsItems.size():5);
        getEventGrid().setColumnExpandRatio(1, 1.0f);
        System.out.println("getEventGrid().getRows() = " + getEventGrid().getRows());

        int i = 0;
        for (NewsItem newsItem : newsItems) {
            Label lblDate = new Label(newsItem.getNewsDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            Label lblText = new Label(newsItem.getNewsText());
            lblText.setWidth("100%");
            getEventGrid().addComponent(lblDate, 0, i);
            getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
            getEventGrid().addComponent(lblText, 1, i);
            i++;
            if(i>4) break;
        }

        getImgTop().setSource(new ThemeResource("images/cards/news.jpg"));
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

}
