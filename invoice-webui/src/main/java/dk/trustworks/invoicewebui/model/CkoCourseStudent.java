package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cko_course_list")
public class CkoCourseStudent {

    @Id
    private String uuid;

    private String useruuid;

    private String status;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate application;

    @ManyToOne()
    @JoinColumn(name="courseuuid")
    private CkoCourse ckoCourse;

    public CkoCourseStudent() {
        this.uuid = UUID.randomUUID().toString();
    }

    public CkoCourseStudent(User member, CkoCourse ckoCourse, String status) {
        this.uuid = UUID.randomUUID().toString();
        this.useruuid = member.getUuid();
        this.ckoCourse = ckoCourse;
        this.status = status;
        this.application = LocalDate.now();
    }

    public String getUuid() {
        return uuid;
    }

    public User getMember() {
        return UserService.get().findByUUID(getUseruuid(), true);
    }

    public void setMember(User member) {
        this.useruuid = member.getUuid();
    }

    public CkoCourse getCkoCourse() {
        return ckoCourse;
    }

    public void setCkoCourse(CkoCourse ckoCourse) {
        this.ckoCourse = ckoCourse;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getApplication() {
        return application;
    }

    public void setApplication(LocalDate application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "MicroCourseStudent{" +
                "uuid=" + uuid +
                ", useruuid='" + useruuid + '\'' +
                ", microCourse=" + ckoCourse +
                '}';
    }
}
