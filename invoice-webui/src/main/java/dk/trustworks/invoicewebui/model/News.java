package dk.trustworks.invoicewebui.model;

import com.google.common.hash.Hashing;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
public class News {

    @Id
    private String uuid;

    private LocalDate newsdate;

    private String description;

    private String newstype;

    private String link;

    private String sha512;

    public News() {
    }

    public News(String description, LocalDate newsdate, String newstype, String link, User user) {
        this.newstype = newstype;
        this.link = link;
        this.uuid = UUID.randomUUID().toString();
        this.description = description;
        this.newsdate = newsdate;
        sha512 = Hashing.sha512().hashString(user.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
    }

    public News(String description, LocalDate newsdate, String newstype, String link, String sha512) {
        this.newstype = newstype;
        this.link = link;
        this.uuid = UUID.randomUUID().toString();
        this.description = description;
        this.newsdate = newsdate;
        this.sha512 = sha512;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDate getNewsdate() {
        return newsdate;
    }

    public void setNewsdate(LocalDate newsdate) {
        this.newsdate = newsdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNewstype() {
        return newstype;
    }

    public void setNewstype(String newstype) {
        this.newstype = newstype;
    }

    @Override
    public String toString() {
        return "News{" +
                "uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", newsdate=" + newsdate +
                ", newstype='" + newstype + '\'' +
                ", link='" + link + '\'' +
                ", sha512='" + sha512 + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(uuid, news.uuid) &&
                Objects.equals(sha512, news.sha512);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, sha512);
    }
}
