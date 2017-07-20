package dk.trustworks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
public class Work {

    @Id private String uuid;
    private int day;
    private int month;
    private int year;
    private String taskuuid;
    private String useruuid;
    private double workduration;
    @JsonIgnore private Timestamp created;

    public Work() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public double getWorkduration() {
        return workduration;
    }

    public void setWorkduration(double workduration) {
        this.workduration = workduration;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", workduration=").append(workduration);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }
}
