package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class News {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime activedate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime enddate;
    private String description;
    private String text;
    private String image;
    private String type;
    private String link;

    public News() {
    }

    public LocalDateTime getActivedate() {
        return activedate;
    }

    public void setActivedate(LocalDateTime activeDate) {
        this.activedate = activeDate;
    }

    public LocalDateTime getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDateTime endDate) {
        this.enddate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
