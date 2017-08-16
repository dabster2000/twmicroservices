package dk.trustworks.invoicewebui.web.time.model;

/**
 * Created by hans on 16/08/2017.
 */
public class WeekItem {

    private String taskname;
    private double mon;
    private double tue;
    private double wed;
    private double thu;
    private double fri;
    private double sat;
    private double sun;
    private double budgetleft;

    public WeekItem() {
    }

    public WeekItem(String taskname, double mon, double tue, double wed, double thu, double fri, double sat, double sun, double budgetleft) {
        this.taskname = taskname;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
        this.budgetleft = budgetleft;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public double getMon() {
        return mon;
    }

    public void setMon(double mon) {
        this.mon = mon;
    }

    public double getTue() {
        return tue;
    }

    public void setTue(double tue) {
        this.tue = tue;
    }

    public double getWed() {
        return wed;
    }

    public void setWed(double wed) {
        this.wed = wed;
    }

    public double getThu() {
        return thu;
    }

    public void setThu(double thu) {
        this.thu = thu;
    }

    public double getFri() {
        return fri;
    }

    public void setFri(double fri) {
        this.fri = fri;
    }

    public double getSat() {
        return sat;
    }

    public void setSat(double sat) {
        this.sat = sat;
    }

    public double getSun() {
        return sun;
    }

    public void setSun(double sun) {
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
        sb.append("taskname='").append(taskname).append('\'');
        sb.append(", mon=").append(mon);
        sb.append(", tue=").append(tue);
        sb.append(", wed=").append(wed);
        sb.append(", thu=").append(thu);
        sb.append(", fri=").append(fri);
        sb.append(", sat=").append(sat);
        sb.append(", sun=").append(sun);
        sb.append(", budgetleft=").append(budgetleft);
        sb.append('}');
        return sb.toString();
    }
}
