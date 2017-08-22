package dk.trustworks.invoicewebui.web.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hans on 21/08/2017.
 */
public class TaskRow {

    private String taskUUID;
    private String taskName;
    private List<TaskRow> userRows = new ArrayList<>();

    private Double[] budget;

    public TaskRow(int months) {
        budget = new Double[months];
    }

    public TaskRow(String taskUUID, String taskName, int months) {
        this(months);
        this.taskUUID = taskUUID;
        this.taskName = taskName;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<TaskRow> getUserRows() {
        return userRows;
    }

    public void setUserRows(List<TaskRow> userRows) {
        this.userRows = userRows;
    }

    public void addUserRow(UserRow userRow) {
        userRows.add(userRow);
    }

    public String getUsername() {
        return "";
    }

    public void setUsername(String username) {
    }

    public double getRate() {
        return 0.0;
    }

    public void setRate(double rate) {
    }

    public Double[] getBudget() {
        return budget;
    }

    public void setBudget(Double[] budget) {
        this.budget = budget;
    }

    public Double getMonth(int month) {
        return budget[month];
    }

    public void setMonth(int month, Double value) {
        budget[month] = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskRow{");
        sb.append("taskUUID='").append(taskUUID).append('\'');
        sb.append(", taskName='").append(taskName).append('\'');
        sb.append(", userRows=").append(userRows);
        sb.append(", budget=").append(Arrays.toString(budget));
        sb.append('}');
        return sb.toString();
    }
}
