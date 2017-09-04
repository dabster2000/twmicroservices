package dk.trustworks.invoicewebui.web.project.model;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Taskworkerconstraint;
import dk.trustworks.invoicewebui.model.User;

/**
 * Created by hans on 21/08/2017.
 */
public class UserRow extends TaskRow {

    private Taskworkerconstraint taskworkerconstraint;
    private User user;
    private String username;
    private String rate;

    public UserRow(int month) {
        super(month);
    }

    public UserRow(Task task, Taskworkerconstraint taskworkerconstraint, int months, User user) {
        super(task, months);
        this.rate = taskworkerconstraint.getPrice()+"";
        this.taskworkerconstraint = taskworkerconstraint;
        this.user = user;
        this.username = user.getUsername();
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Taskworkerconstraint getTaskworkerconstraint() {
        return taskworkerconstraint;
    }

    public void setTaskworkerconstraint(Taskworkerconstraint taskworkerconstraint) {
        this.taskworkerconstraint = taskworkerconstraint;
    }

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
