package dk.trustworks.invoicewebui.web.dashboard.cards;

public class TopCardContent {

    private final String icon;
    private final String title;
    private final String subTitle;
    private final String number;
    private final String style;

    public TopCardContent(String icon, String title, String subTitle, String number, String style) {
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
        this.number = number;
        this.style = style;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getNumber() {
        return number;
    }

    public String getStyle() {
        return style;
    }
}
