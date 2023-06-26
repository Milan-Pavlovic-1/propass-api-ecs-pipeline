package net.altitudetech.propass.api.gateway.dto;

import java.time.LocalDateTime;
import org.springframework.lang.NonNull;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO extends BaseDTO {
  private Long airlineId;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime departureDate;
  @NonNull
  private FlightLocationDTO locationFrom;
  @NonNull
  private FlightLocationDTO locationTo;
  
  @Builder
  public FlightDTO(Long id, Long airlineId, FlightLocationDTO locationFrom,
      FlightLocationDTO locationTo, LocalDateTime departureDate) {
    super(id);
    this.airlineId = airlineId;
    this.locationFrom = locationFrom;
    this.locationTo = locationTo;
    this.departureDate = departureDate;
  }
  
}