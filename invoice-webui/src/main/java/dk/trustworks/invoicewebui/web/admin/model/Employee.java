package dk.trustworks.invoicewebui.web.admin.model;

public class Employee {
    String name;
    String status;
    int hours;
    int salary;

    public Employee(String name, String status, int hours, int salary) {
        this.name = name;
        this.status = status;
        this.hours = hours;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
