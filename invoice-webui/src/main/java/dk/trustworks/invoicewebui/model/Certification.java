package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    private String certification;

    private String level;

    private int year;

    public Certification() {
    }

    public Certification(String useruuid, String certification, String level, int year) {
        this.useruuid = useruuid;
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

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
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
                ", useruuid='" + useruuid + '\'' +
                ", certification='" + certification + '\'' +
                ", level='" + level + '\'' +
                ", year=" + year +
                '}';
    }
}
