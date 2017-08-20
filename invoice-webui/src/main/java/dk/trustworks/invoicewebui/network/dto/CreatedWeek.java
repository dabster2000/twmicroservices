package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by hans on 28/06/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatedWeek {

    private String uuid;
    private int weeknumber;
    private int year;
    private int sorting;
    private String useruuid;
    private String taskuuid;

    public CreatedWeek() {
    }

    public CreatedWeek(String uuid, int weeknumber, int year, String useruuid, String taskuuid) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.useruuid = useruuid;
        this.taskuuid = taskuuid;
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

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CreatedWeek{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", weeknumber=").append(weeknumber);
        sb.append(", year=").append(year);
        sb.append(", sorting=").append(sorting);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
