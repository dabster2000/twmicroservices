package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.BubbleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bubble {

    private String uuid;
    private String name;
    @Enumerated(EnumType.STRING)
    private BubbleType type;
    private String description;
    private String application;
    private String slackchannel;

    private String owner;

    private String coowner;

    private String meetingform;

    private String preconditions;

    private boolean active;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate created;

    @JsonProperty("bubbleMembers")
    public List<BubbleMember> bubbleMembers;

}

