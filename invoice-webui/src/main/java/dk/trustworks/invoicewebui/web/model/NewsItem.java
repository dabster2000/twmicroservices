package dk.trustworks.invoicewebui.web.model;

import java.time.LocalDate;

/**
 * Created by hans on 13/09/2017.
 */
public class NewsItem {

    private LocalDate newsDate;
    private String newsText;

    public NewsItem() {
    }

    public NewsItem(LocalDate newsDate, String newsText) {
        this.newsDate = newsDate;
        this.newsText = newsText;
    }

    public LocalDate getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(LocalDate newsDate) {
        this.newsDate = newsDate;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    @Override
    public String toString() {
        return "NewsItem{" + "newsDate=" + newsDate +
                ", newsText='" + newsText + '\'' +
                '}';
    }
}
