package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.web.model.StatusItem;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 11/08/2017.
 */
public class StatusImpl extends StatusDesign implements Box {


    private int priority;
    private int boxWidth;
    private String name;

    public StatusImpl(ProjectRepository projectRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;
        org.joda.time.LocalDate dateThreeMonthsAgo = org.joda.time.LocalDate.now().minusMonths(3);

        getEventGrid().setColumnExpandRatio(1, 1.0f);
        List<StatusItem> statusItems = new ArrayList<>();
        for (Project project : projectRepository.findAllByActiveTrueOrderByNameAsc()) {
            boolean projectHasWork = false;
            taskLoop:
            for (Task task : project.getTasks()) {
                for (Work work : task.getWorkList()) {
                    org.joda.time.LocalDate workDate = new org.joda.time.LocalDate(work.getYear(), work.getMonth()+1, work.getDay());
                    if(workDate.isAfter(dateThreeMonthsAgo)) {
                        projectHasWork = true;
                        break taskLoop;
                    }
                }
            }
            if(!projectHasWork) {
                statusItems.add(new StatusItem(shortname(project.getOwner().getFirstname()+" "+project.getOwner().getLastname()),
                        "No work has been done on the project '"+project.getName()+"'. Consider making it inactive.",
                        ProjectManagerView.VIEW_NAME+"/"+project.getUuid()));
            }
        }

        getEventGrid().setRows((statusItems.size()<6)?statusItems.size():5);
        getEventGrid().setColumnExpandRatio(1, 1.0f);
        System.out.println("getEventGrid().getRows() = " + getEventGrid().getRows());

        int i = 0;
        for (StatusItem statusItem : statusItems) {
            Label lblDate = new Label(statusItem.getStatusKey());
            Label lblText = new Label(statusItem.getStatusText());
            lblText.addContextClickListener(event -> UI.getCurrent().getNavigator().navigateTo(statusItem.getLink()));
            lblText.setWidth("100%");
            getEventGrid().addComponent(lblDate, 0, i);
            getEventGrid().setComponentAlignment(lblDate, Alignment.TOP_RIGHT);
            getEventGrid().addComponent(lblText, 1, i);
            i++;
            if(i>4) break;
        }

        getImgTop().setSource(new ThemeResource("images/cards/project-status.jpg"));
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

    public String shortname(String longName) {
        StringBuilder shortname = new StringBuilder();
        for (String s : longName.split(" ")) {
            shortname.append(s.charAt(0));
        }
        return shortname.toString();
    }

}
