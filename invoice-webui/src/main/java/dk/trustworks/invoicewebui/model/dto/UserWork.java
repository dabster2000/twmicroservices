package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserWork {

    private final User user;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate from;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate to;

    private final List<ProjectWork> projectWorkList;

    public UserWork(User user, LocalDate from, LocalDate to) {
        this.user = user;
        this.from = from;
        this.to = to;
        this.projectWorkList = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public List<ProjectWork> getProjectWorkList() {
        return projectWorkList;
    }

    @Override
    public String toString() {
        return "UserWork{" +
                "projectWorkList=" + projectWorkList.size() +
                '}';
    }
}
