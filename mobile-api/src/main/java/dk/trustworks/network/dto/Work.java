package dk.trustworks.network.dto;

/**
 * Created by hans on 28/06/2017.
 */
public class Work {

    private String uuid;
    private int day;
    private int month;
    private int year;
    private double workduration;

    public Work() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getWorkduration() {
        return workduration;
    }

    public void setWorkduration(double workduration) {
        this.workduration = workduration;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", workduration=").append(workduration);
        sb.append('}');
        return sb.toString();
    }
}
