package dk.trustworks.invoicewebui.web.time.model;

/**
 * Created by hans on 16/08/2017.
 */
public class WeekItem {

    private String taskuuid;
    private String useruuid;
    private String taskname;
    private String mon;
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;
    private String sun;
    private double budgetleft;

    public WeekItem() {
    }

    public WeekItem(String taskuuid, String useruuid) {
        this.taskuuid = taskuuid;
        this.useruuid = useruuid;
    }

    public String getTaskuuid() {
        return taskuuid;
    }

    public void setTaskuuid(String taskuuid) {
        this.taskuuid = taskuuid;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public double getBudgetleft() {
        return budgetleft;
    }

    public void setBudgetleft(double budgetleft) {
        this.budgetleft = budgetleft;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WeekItem{");
        sb.append("taskuuid='").append(taskuuid).append('\'');
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskname='").append(taskname).append('\'');
        sb.append(", mon='").append(mon).append('\'');
        sb.append(", tue='").append(tue).append('\'');
        sb.append(", wed='").append(wed).append('\'');
        sb.append(", thu='").append(thu).append('\'');
        sb.append(", fri='").append(fri).append('\'');
        sb.append(", sat='").append(sat).append('\'');
        sb.append(", sun='").append(sun).append('\'');
        sb.append(", budgetleft=").append(budgetleft);
        sb.append('}');
        return sb.toString();
    }
}
