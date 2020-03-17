
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
    "vatCode",
    "self"
})
public class VatAccount {

    @JsonProperty("vatCode")
    private String vatCode;
    @JsonProperty("self")
    private String self;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public VatAccount() {
    }

    /**
     * 
     * @param self
     * @param vatCode
     */
    public VatAccount(String vatCode, String self) {
        super();
        this.vatCode = vatCode;
        this.self = self;
    }

    @JsonProperty("vatCode")
    public String getVatCode() {
        return vatCode;
    }

    @JsonProperty("vatCode")
    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    @JsonProperty("self")
    public String getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(String self) {
        this.self = self;
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
        return new ToStringBuilder(this).append("vatCode", vatCode).append("self", self).append("additionalProperties", additionalProperties).toString();
    }

}
