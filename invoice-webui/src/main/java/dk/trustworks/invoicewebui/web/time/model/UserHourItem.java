package dk.trustworks.invoicewebui.web.time.model;

/**
 * Created by hans on 10/09/2017.
 */
public class UserHourItem {

    private String userUUID;
    private String taskUUID;
    private String projectTaskName;
    private String username;
    private double hours;

    public UserHourItem() {
    }

    public UserHourItem(String userUUID, String taskUUID, String projectTaskName, String username) {
        this.userUUID = userUUID;
        this.taskUUID = taskUUID;
        this.projectTaskName = projectTaskName;
        this.username = username;
        hours = 0.0;
    }

    public String getProjectTaskName() {
        return projectTaskName;
    }

    public void setProjectTaskName(String projectTaskName) {
        this.projectTaskName = projectTaskName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public void addHours(double hours) {
        this.hours += hours;
    }

    @Override
    public String toString() {
        return "UserHourItem{" + "userUUID='" + userUUID + '\'' +
                ", taskUUID='" + taskUUID + '\'' +
                ", projectTaskName='" + projectTaskName + '\'' +
                ", username='" + username + '\'' +
                ", hours=" + hours +
                '}';
    }
}
