package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.TaskService;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
public class WorkWithRate {

    @Id
    private int id;
    private int day;
    private int month;
    private int year;
    private double workduration;
    private double rate;

    private String taskuuid;

    private String useruuid;

    private String workas;

    public WorkWithRate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getWorkduration() {
        return workduration;
    }

    public void setWorkduration(double workduration) {
        this.workduration = workduration;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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

    public LocalDate getDate() {
        return LocalDate.of(year, month+1, day);
    }

    @Override
    public String toString() {
        return "WorkWithRate{" +
                "id=" + id +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", workduration=" + workduration +
                ", rate=" + rate +
                ", taskuuid='" + taskuuid + '\'' +
                ", useruuid='" + useruuid + '\'' +
                ", workas='" + workas + '\'' +
                '}';
    }
}
