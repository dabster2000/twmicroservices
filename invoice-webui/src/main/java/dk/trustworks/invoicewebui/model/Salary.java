package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by hans on 23/06/2017.
 */

@Entity
public class Salary {

    @Id
    private String uuid;
    @ManyToOne()
    @JoinColumn(name="useruuid")
    private User user;
    private int salary;
    private Date activefrom;

    public Salary() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Date getActivefrom() {
        return activefrom;
    }

    public void setActivefrom(Date activefrom) {
        this.activefrom = activefrom;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Salary{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", user=").append(user);
        sb.append(", salary=").append(salary);
        sb.append(", activefrom=").append(activefrom);
        sb.append('}');
        return sb.toString();
    }
}
