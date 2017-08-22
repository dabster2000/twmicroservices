package dk.trustworks.invoicewebui.network.dto;

public class Taskworkerconstraint {
    private String uuid;
    private Double price;

    private Task task;
    private String useruuid;
    private String taskuuid;

    public Taskworkerconstraint() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Taskworkerconstraint{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", price=").append(price);
        sb.append(", task=").append(task);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
