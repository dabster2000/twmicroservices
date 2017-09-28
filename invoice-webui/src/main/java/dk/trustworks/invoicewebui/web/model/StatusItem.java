package dk.trustworks.invoicewebui.web.model;

/**
 * Created by hans on 13/09/2017.
 */
public class StatusItem {

    private String statusKey;
    private String statusText;
    private String link;

    public StatusItem() {
    }

    public StatusItem(String statusKey, String statusText, String link) {
        this.statusKey = statusKey;
        this.statusText = statusText;
        this.link = link;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "StatusItem{" +
                "statusKey='" + statusKey + '\'' +
                ", statusText='" + statusText + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
