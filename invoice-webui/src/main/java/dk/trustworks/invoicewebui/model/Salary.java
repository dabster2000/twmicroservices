package dk.trustworks.invoicewebui.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */
public class Salary {

    private String uuid;
    private int salary;

    private LocalDate activefrom;

    public Salary() {
    }

    public Salary(LocalDate activeFrom, int salary) {
        uuid = UUID.randomUUID().toString();
        this.activefrom = activeFrom;
        this.salary = salary;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public LocalDate getActivefrom() {
        return activefrom;
    }

    public void setActivefrom(LocalDate activefrom) {
        this.activefrom = activefrom;
    }

    @Override
    public String toString() {
        return "Salary{" + "uuid='" + uuid + '\'' +
                ", salary=" + salary +
                ", activefrom=" + activefrom +
                '}';
    }
}
