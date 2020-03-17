
package dk.trustworks.invoicewebui.network.clients.model.economics;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "maxPageSizeAllowed",
    "skipPages",
    "pageSize",
    "results",
    "resultsWithoutFilter",
    "firstPage",
    "lastPage"
})
public class Pagination {

    @JsonProperty("maxPageSizeAllowed")
    private int maxPageSizeAllowed;
    @JsonProperty("skipPages")
    private int skipPages;
    @JsonProperty("pageSize")
    private int pageSize;
    @JsonProperty("results")
    private int results;
    @JsonProperty("resultsWithoutFilter")
    private int resultsWithoutFilter;
    @JsonProperty("firstPage")
    private String firstPage;
    @JsonProperty("lastPage")
    private String lastPage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Pagination() {
    }

    /**
     * 
     * @param firstPage
     * @param lastPage
     * @param maxPageSizeAllowed
     * @param resultsWithoutFilter
     * @param skipPages
     * @param pageSize
     * @param results
     */
    public Pagination(int maxPageSizeAllowed, int skipPages, int pageSize, int results, int resultsWithoutFilter, String firstPage, String lastPage) {
        super();
        this.maxPageSizeAllowed = maxPageSizeAllowed;
        this.skipPages = skipPages;
        this.pageSize = pageSize;
        this.results = results;
        this.resultsWithoutFilter = resultsWithoutFilter;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    @JsonProperty("maxPageSizeAllowed")
    public int getMaxPageSizeAllowed() {
        return maxPageSizeAllowed;
    }

    @JsonProperty("maxPageSizeAllowed")
    public void setMaxPageSizeAllowed(int maxPageSizeAllowed) {
        this.maxPageSizeAllowed = maxPageSizeAllowed;
    }

    @JsonProperty("skipPages")
    public int getSkipPages() {
        return skipPages;
    }

    @JsonProperty("skipPages")
    public void setSkipPages(int skipPages) {
        this.skipPages = skipPages;
    }

    @JsonProperty("pageSize")
    public int getPageSize() {
        return pageSize;
    }

    @JsonProperty("pageSize")
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("results")
    public int getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(int results) {
        this.results = results;
    }

    @JsonProperty("resultsWithoutFilter")
    public int getResultsWithoutFilter() {
        return resultsWithoutFilter;
    }

    @JsonProperty("resultsWithoutFilter")
    public void setResultsWithoutFilter(int resultsWithoutFilter) {
        this.resultsWithoutFilter = resultsWithoutFilter;
    }

    @JsonProperty("firstPage")
    public String getFirstPage() {
        return firstPage;
    }

    @JsonProperty("firstPage")
    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    @JsonProperty("lastPage")
    public String getLastPage() {
        return lastPage;
    }

    @JsonProperty("lastPage")
    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("maxPageSizeAllowed", maxPageSizeAllowed).append("skipPages", skipPages).append("pageSize", pageSize).append("results", results).append("resultsWithoutFilter", resultsWithoutFilter).append("firstPage", firstPage).append("lastPage", lastPage).append("additionalProperties", additionalProperties).toString();
    }

}
