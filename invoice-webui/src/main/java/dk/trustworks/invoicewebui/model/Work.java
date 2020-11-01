package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.services.TaskService;
import dk.trustworks.invoicewebui.services.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Work {

    private String uuid;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate registered;
    private double workduration;
    private String clientuuid;
    private String projectuuid;
    private String taskuuid;
    private String contractuuid;
    private String useruuid;
    private String workas;
    private double rate;

    public Work(LocalDate registered, double workduration, User user, Task task) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = user.getUuid();
        this.taskuuid = task.getUuid();
    }

    public Work(LocalDate registered, double workduration, User user, Task task, User workas) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = user.getUuid();
        this.taskuuid = task.getUuid();
        this.workas = workas.getUuid();
    }

    public Task getTask() {
        return TaskService.get().findOne(taskuuid);
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid(), true);
    }

    public User getWorkasUser() {
        if(getWorkas()==null || getWorkas().trim().equals("")) return null;
        return UserService.get().findByUUID(getWorkas(), true);
    }
}
