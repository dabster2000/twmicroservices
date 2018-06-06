package dk.trustworks.invoicewebui.web.time.model;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Week;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import org.joda.time.LocalDate;

/**
 * Created by hans on 16/08/2017.
 */
public class WeekItem {

    private Week week;
    private Task task;
    private User user;
    private String taskname;
    private String mon = "0,0";
    private String tue = "0,0";
    private String wed = "0,0";
    private String thu = "0,0";
    private String fri = "0,0";
    private String sat = "0,0";
    private String sun = "0,0";
    private double budgetleft;
    private LocalDate date;
    private User workas;
    private boolean locked = false;

    public WeekItem() {
    }

    public WeekItem(Week week, Task task, User user, boolean locked) {
        this.week = week;
        this.task = task;
        this.user = user;
        this.locked = locked;
    }

    public WeekItem(Week week, Task task, User user, User workas, boolean locked) {
        this.week = week;
        this.task = task;
        this.user = user;
        this.workas = workas;
        this.locked = locked;
    }

    public Week getWeek() {
        return week;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public User getWorkas() {
        return workas;
    }

    public void setWorkas(User workas) {
        this.workas = workas;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public double getWeekItemSum() {
        return NumberConverter.parseDouble(mon) +
                NumberConverter.parseDouble(tue) +
                NumberConverter.parseDouble(wed) +
                NumberConverter.parseDouble(thu) +
                NumberConverter.parseDouble(fri) +
                NumberConverter.parseDouble(sat) +
                NumberConverter.parseDouble(sun);
    }

    public double getBudgetleft() {
        return budgetleft;
    }

    public void setBudgetleft(double budgetleft) {
        this.budgetleft = budgetleft;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeekItem{" +
                "task=" + task +
                ", user=" + user.getUsername() +
                ", taskname='" + taskname + '\'' +
                ", mon='" + mon + '\'' +
                ", tue='" + tue + '\'' +
                ", wed='" + wed + '\'' +
                ", thu='" + thu + '\'' +
                ", fri='" + fri + '\'' +
                ", sat='" + sat + '\'' +
                ", sun='" + sun + '\'' +
                ", budgetleft=" + budgetleft +
                ", date=" + date +
                ", workas=" + workas +
                '}';
    }
}
