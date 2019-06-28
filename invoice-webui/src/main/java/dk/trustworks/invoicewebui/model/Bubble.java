package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bubbles")
public class Bubble {

    @Id
    private String uuid;
    private String name;
    private String description;
    private String application;
    private String slackchannel;

    private String useruuid;

    @Transient
    private User user;
    private boolean active;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate created;

    //@OneToMany(mappedBy = "bubble", fetch = FetchType.LAZY)
    //private List<BubbleMember> bubbleMembers;

    public Bubble() {
        this.uuid = UUID.randomUUID().toString();
        this.created = LocalDate.now();
        this.active = true;
    }

    public Bubble(String name, String description, String application, User user, boolean active, String slackchannel) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.application = application;
        this.useruuid = user.getUuid();
        this.active = active;
        this.slackchannel = slackchannel;
        this.created = LocalDate.now();
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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getSlackchannel() {
        return slackchannel;
    }

    public void setSlackchannel(String slackchannel) {
        this.slackchannel = slackchannel;
    }
/*
    public List<BubbleMember> getBubbleMembers() {
        return bubbleMembers;
    }

    public void setBubbleMembers(List<BubbleMember> bubbleMembers) {
        this.bubbleMembers = bubbleMembers;
    }
*/
    @Override
    public String toString() {
        return "Bubble{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", application='" + application + '\'' +
                ", slackchannel='" + slackchannel + '\'' +
                ", user=" + user.getUsername() +
                ", active=" + active +
                ", created=" + created +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
