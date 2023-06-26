
package net.altitudetech.propass.flight.booking.sabre.dto.integration.response;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "marketing",
    "marketingFlightNumber",
    "operating",
    "operatingFlightNumber",
    "equipment"
})
public class Carrier {

    @JsonProperty("marketing")
    public String marketing;
    @JsonProperty("marketingFlightNumber")
    public Integer marketingFlightNumber;
    @JsonProperty("operating")
    public String operating;
    @JsonProperty("operatingFlightNumber")
    public Integer operatingFlightNumber;
    @JsonProperty("equipment")
    public Equipment equipment;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
