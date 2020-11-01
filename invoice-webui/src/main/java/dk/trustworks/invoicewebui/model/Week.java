package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import dk.trustworks.invoicewebui.services.TaskService;
import dk.trustworks.invoicewebui.services.UserService;

/**
 * Created by hans on 28/06/2017.
 */

public class Week {

    private String uuid;
    private String taskuuid;
    private String useruuid;
    private int weeknumber;
    private int year;
    private int sorting;
    private String workas;

    public Week() {
    }

    public Week(String uuid, int weeknumber, int year, String useruuid, String taskuuid) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.useruuid = useruuid;
        this.taskuuid = taskuuid;
    }

    public Week(String uuid, int weeknumber, int year, String useruuid, String taskuuid, String workas) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.useruuid = useruuid;
        this.taskuuid = taskuuid;
        this.workas = (workas!=null)?workas:null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getWeeknumber() {
        return weeknumber;
    }

    public void setWeeknumber(int weeknumber) {
        this.weeknumber = weeknumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    @JsonIgnore
    public Task getTask() {
        return TaskService.get().findOne(taskuuid);
    }

    public String getWorkas() {
        return workas;
    }

    public void setWorkas(String workas) {
        this.workas = workas;
    }

    @JsonIgnore
    public User getWorkasUser() {
        return UserService.get().findByUUID(getWorkas(), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        return getWeeknumber() == week.getWeeknumber() &&
                getYear() == week.getYear() &&
                Objects.equal(getTask(), week.getTask()) &&
                Objects.equal(getUseruuid(), week.getUseruuid()) &&
                Objects.equal(getWorkas(), week.getWorkas());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTask(), getUseruuid(), getWeeknumber(), getYear(), getWorkas());
    }

    @Override
    public String toString() {
        return "Week{" +
                "uuid='" + uuid + '\'' +
                ", taskuuid='" + taskuuid + '\'' +
                ", useruuid='" + useruuid + '\'' +
                ", weeknumber=" + weeknumber +
                ", year=" + year +
                ", sorting=" + sorting +
                ", workas='" + workas + '\'' +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
