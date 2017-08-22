package dk.trustworks.invoicewebui.network.dto;

import java.sql.Timestamp;

public class Budget {
    private String uuid;
    private Long month;
    private Long year;
    private Timestamp created;
    private Double budget;
    private String useruuid;
    private String taskuuid;

    public Budget() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
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
        final StringBuilder sb = new StringBuilder("Budget{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", created=").append(created);
        sb.append(", budget=").append(budget);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
