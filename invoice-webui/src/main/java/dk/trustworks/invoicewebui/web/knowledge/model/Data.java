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

public class Data {

    private String headline;
    private String description;
    private String color;
    private Item[] items;


    public Data() {
    }

    public Data(String headline, String description, String color, Item[] items) {
        this.headline = headline;
        this.description = description;
        this.color = color;
        this.items = items;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}