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

public class DocumentMetadata {

    private String headline;
    private String filetype;
    private String file;
    private String preview;
    private String description;
    private String[] authors;
    private String customeruuid;
    private String projectuuid;
    private String date;


    public DocumentMetadata() {
    }

    public DocumentMetadata(String headline, String filetype, String file, String preview, String description, String[] authors, String customeruuid, String projectuuid, String date) {
        this.headline = headline;
        this.filetype = filetype;
        this.file = file;
        this.preview = preview;
        this.description = description;
        this.authors = authors;
        this.customeruuid = customeruuid;
        this.projectuuid = projectuuid;
        this.date = date;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getCustomeruuid() {
        return customeruuid;
    }

    public void setCustomeruuid(String customeruuid) {
        this.customeruuid = customeruuid;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}