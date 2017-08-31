package dk.trustworks.model;

/**
 * Created by hans on 30/08/2017.
 */
public class TaskWithExpandedName extends Task {

    private String clientName;
    private String projectName;

    public TaskWithExpandedName() {
    }

    public TaskWithExpandedName(Task task) {
        this.setUuid(task.getUuid());
        this.setName(task.getName());
        this.setProjectuuid(task.getProjectuuid());
        this.setType(task.getType());
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
