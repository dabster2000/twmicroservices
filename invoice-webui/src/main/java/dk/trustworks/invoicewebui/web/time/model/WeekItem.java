package dk.trustworks.invoicewebui.web.time.model;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;

/**
 * Created by hans on 16/08/2017.
 */
public class WeekItem {

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

    public WeekItem() {
    }

    public WeekItem(Task task, User user) {
        this.task = task;
        this.user = user;
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

    public double getBudgetleft() {
        return budgetleft;
    }

    public void setBudgetleft(double budgetleft) {
        this.budgetleft = budgetleft;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WeekItem{");
        sb.append(", taskname='").append(taskname).append('\'');
        sb.append(", mon='").append(mon).append('\'');
        sb.append(", tue='").append(tue).append('\'');
        sb.append(", wed='").append(wed).append('\'');
        sb.append(", thu='").append(thu).append('\'');
        sb.append(", fri='").append(fri).append('\'');
        sb.append(", sat='").append(sat).append('\'');
        sb.append(", sun='").append(sun).append('\'');
        sb.append(", budgetleft=").append(budgetleft);
        sb.append('}');
        return sb.toString();
    }
}
