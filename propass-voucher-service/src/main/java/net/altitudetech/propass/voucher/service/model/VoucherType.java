package net.altitudetech.propass.voucher.service.model;

import org.hibernate.annotations.Type;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.model.AuditableModel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vouchers_type")
public class VoucherType extends AuditableModel {
  @Column(nullable = false)
  private String name;
  
  @Column(nullable = false)
  private Long airlineId;
  
  @Type(JsonType.class)
  @Column(columnDefinition = "json")
  private JsonNode priceFormula;
  
  @Type(JsonType.class)
  @Column(columnDefinition = "json")
  private JsonNode restrictionFormula;
}
