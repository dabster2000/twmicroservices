package dk.trustworks.invoicewebui.model.rest;

import java.util.Arrays;

public class Dataset_data
{
    private String limit;

    private String[] column_names;

    private String transform;

    private String end_date;

    private String order;

    private String[][] data;

    private String frequency;

    private String column_index;

    private String start_date;

    private String collapse;

    public Dataset_data() {
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String[] getColumn_names() {
        return column_names;
    }

    public void setColumn_names(String[] column_names) {
        this.column_names = column_names;
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getColumn_index() {
        return column_index;
    }

    public void setColumn_index(String column_index) {
        this.column_index = column_index;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getCollapse() {
        return collapse;
    }

    public void setCollapse(String collapse) {
        this.collapse = collapse;
    }

    @Override
    public String toString() {
        return "Dataset_data{" +
                "limit='" + limit + '\'' +
                ", column_names=" + Arrays.toString(column_names) +
                ", transform='" + transform + '\'' +
                ", end_date='" + end_date + '\'' +
                ", order='" + order + '\'' +
                ", data=" + Arrays.toString(data) +
                ", frequency='" + frequency + '\'' +
                ", column_index='" + column_index + '\'' +
                ", start_date='" + start_date + '\'' +
                ", collapse='" + collapse + '\'' +
                '}';
    }
}
