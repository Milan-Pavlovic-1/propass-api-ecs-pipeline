package net.altitudetech.propass.flight.service.model;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.model.AuditableModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "flights")
public class Flight extends AuditableModel {
    @Column(nullable = false, updatable = false)
    private Long airlineId;

    @Embedded
    @AttributeOverrides({
      @AttributeOverride(name = "name", column = @Column(name = "from_location_name")),
      @AttributeOverride(name = "code", column = @Column(name = "from_location_code")), 
      @AttributeOverride(name = "type", column = @Column(name = "from_location_type"))
    })
    private FlightLocation locationFrom;

    @Embedded
    @AttributeOverrides({
      @AttributeOverride(name = "name", column = @Column(name = "to_location_name")),
      @AttributeOverride(name = "code", column = @Column(name = "to_location_code")), 
      @AttributeOverride(name = "type", column = @Column(name = "to_location_type"))
    })
    private FlightLocation locationTo;
}