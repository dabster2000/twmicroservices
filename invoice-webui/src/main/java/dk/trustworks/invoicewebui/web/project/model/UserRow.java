package dk.trustworks.invoicewebui.web.project.model;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.utils.NumberConverter;

/**
 * Created by hans on 21/08/2017.
 */
public class UserRow extends TaskRow {

    //private Taskworkerconstraint taskworkerconstraint;
    private User user;
    private String username;
    private String rate;

    public UserRow(int month) {
        super(month);
    }

    public UserRow(Task task, int months, double rate, User user) {
        super(task, months);
        this.rate = NumberConverter.formatDouble(rate);//taskworkerconstraint.getPrice()+"";
        //this.taskworkerconstraint = taskworkerconstraint;
        this.user = user;
        this.username = user.getUsername();
    }

    @Override
    public void setTaskName(String taskName) {
        return;
    }

    @Override
    public void setMonth(int month, String value) {
        getBudget()[month] = value;
    }

    @Override
    public String getMonth(int actualMonth) {
        return (super.getMonth(actualMonth)!=null)?super.getMonth(actualMonth):"0.0";
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
/*
    public Taskworkerconstraint getTaskworkerconstraint() {
        return taskworkerconstraint;
    }

    public void setTaskworkerconstraint(Taskworkerconstraint taskworkerconstraint) {
        this.taskworkerconstraint = taskworkerconstraint;
    }
*/
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserRow{");
        sb.append(super.toString());
        sb.append(", username='").append(username).append('\'');
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }
}
