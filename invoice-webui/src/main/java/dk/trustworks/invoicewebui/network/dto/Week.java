package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by hans on 28/06/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Week extends ResourceSupport {

    private String uuid;
    private int weeknumber;
    private int year;
    private int sorting;
    private User user;
    private Task task;

    public Week() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getWeeknumber() {
        return weeknumber;
    }

    public void setWeeknumber(int weeknumber) {
        this.weeknumber = weeknumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Week{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", weeknumber=").append(weeknumber);
        sb.append(", year=").append(year);
        sb.append(", sorting=").append(sorting);
        sb.append(", user=").append(user);
        sb.append('}');
        return sb.toString();
    }
}
