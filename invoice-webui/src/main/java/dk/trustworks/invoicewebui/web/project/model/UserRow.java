package dk.trustworks.invoicewebui.web.project.model;

import dk.trustworks.invoicewebui.network.dto.Task;
import dk.trustworks.invoicewebui.network.dto.Taskworkerconstraint;

/**
 * Created by hans on 21/08/2017.
 */
public class UserRow extends TaskRow {

    private Taskworkerconstraint taskworkerconstraint;
    private String userUUID;
    private String username;
    private String rate;

    public UserRow(int month) {
        super(month);
    }

    public UserRow(Task task, Taskworkerconstraint taskworkerconstraint, int months, String userUUID, String username) {
        super(task, months);
        this.taskworkerconstraint = taskworkerconstraint;
        this.userUUID = userUUID;
        this.username = username;
        this.rate = taskworkerconstraint.getPrice()+"";
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserRow{");
        sb.append(super.toString());
        sb.append("userUUID='").append(userUUID).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }

    public Taskworkerconstraint getTaskworkerconstraint() {
        return taskworkerconstraint;
    }

    public void setTaskworkerconstraint(Taskworkerconstraint taskworkerconstraint) {
        this.taskworkerconstraint = taskworkerconstraint;
    }
}
