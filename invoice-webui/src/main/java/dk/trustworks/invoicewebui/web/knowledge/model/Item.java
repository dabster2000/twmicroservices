package dk.trustworks.invoicewebui.web.knowledge.model;

/*
{
  "headline": "DSB Systemkort",
  "filetype": "pptx"
  "description": "Peter og Simon udarbejdede et systemkort til trafikinformationen således kunden kunne danne et overblik over hvordan data “flød” igennem systemerne og endte hos slubrugeren. Beskrivelse af systemerne fandtes sted i tilhørende excelark.",
  "authors": ["64035502-bcfc-11e5-9912-ba0be0483c18"],
  "customeruuid": "4734ff94-48eb-4362-9b7f-53b30466c4ec",
  "projectuuid": "55bc21a0-5eff-44d7-bfec-460410af44c7",
  "date": "2017-02-15"
}
 */

public class Item {

    private String key;
    private String title;
    private String type;
    private String content;


    public Item() {
    }

    public Item(String key, String title, String type, String content) {
        this.key = key;
        this.title = title;
        this.type = type;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}