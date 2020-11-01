package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cko_courses")
public class CkoCourse {

    @Id
    private String uuid;
    private String name;
    private String description;
    private String type;
    private String owner;
    private boolean active;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate created;

    @OneToMany(mappedBy = "ckoCourse")
    private List<CkoCourseStudent> students;

    public CkoCourse() {
        this.uuid = UUID.randomUUID().toString();
        this.created = LocalDate.now();
        this.active = true;
        this.students = new ArrayList<>();
    }

    public CkoCourse(String type) {
        this();
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return UserService.get().findByUUID(getOwner(), true);
    }

    public void setUser(User user) {
        this.owner = user.getUuid();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getCreated() {
        return created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CkoCourseStudent> getStudents() {
        return students;
    }

    public void setStudents(List<CkoCourseStudent> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", active=" + active +
                ", created=" + created +
                '}';
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
