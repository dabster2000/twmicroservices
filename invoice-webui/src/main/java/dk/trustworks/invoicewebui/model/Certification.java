package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    @Transient
    private User user;

    private String certification;

    private String level;

    private int year;

    public Certification() {
    }

    public Certification(User user, String certification, String level, int year) {
        this.useruuid = user.getUuid();
        this.certification = certification;
        this.level = level;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Certification{" +
                "id=" + id +
                ", user=" + user +
                ", certification='" + certification + '\'' +
                ", level='" + level + '\'' +
                ", year=" + year +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
