package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.trustworks.invoicewebui.model.enums.ConferenceApplicationStatus;
import dk.trustworks.invoicewebui.model.enums.ConferenceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceParticipant {

    private String uuid;
    private String conferenceuuid;
    private String name;
    private String company;
    private String titel;
    private String email;
    private boolean samtykke;
    private ConferenceType type;
    private ConferenceApplicationStatus status;
    private Client client;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registered;

}
