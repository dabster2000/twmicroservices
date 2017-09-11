package dk.trustworks.invoicewebui.web.time.model;

/**
 * Created by hans on 10/09/2017.
 */
public class BudgetRemainingItem {

    private String userUUID;
    private String taskUUID;
    private String projectTaskName;
    private String username;
    private double usedBudget;
    private double totalBudget;

    public BudgetRemainingItem() {
    }

    public BudgetRemainingItem(String userUUID, String taskUUID, String projectTaskName, String username) {
        this.userUUID = userUUID;
        this.taskUUID = taskUUID;
        this.projectTaskName = projectTaskName;
        this.username = username;
        usedBudget = 0.0;
        totalBudget = 0.0;
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

    public double getUsedBudget() {
        return usedBudget;
    }

    public void setUsedBudget(double usedBudget) {
        this.usedBudget = usedBudget;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public void addUsedBudget(double usedBudget) {
        this.usedBudget += usedBudget;
    }

    public void addTotalBudget(double totalBudget) {
        this.totalBudget += totalBudget;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BudgetRemainingItem{");
        sb.append("userUUID='").append(userUUID).append('\'');
        sb.append(", taskUUID='").append(taskUUID).append('\'');
        sb.append(", projectTaskName='").append(projectTaskName).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", usedBudget=").append(usedBudget);
        sb.append(", totalBudget=").append(totalBudget);
        sb.append('}');
        return sb.toString();
    }
}
