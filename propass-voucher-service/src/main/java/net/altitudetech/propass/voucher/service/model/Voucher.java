package net.altitudetech.propass.voucher.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity(name = "vouchers")
public class Voucher extends AuditableModel {
  @Column(nullable = false, updatable = false)
  private Integer totalAmount;
  @Builder.Default
  @Column(nullable = false)
  private Integer usedAmount = 0;
  @Column(nullable = false, updatable = false)
  private Long flightId;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "voucher_type_id",
      foreignKey = @ForeignKey(name = "vouchers_voucher_type_id_fk"), updatable = false)
  private VoucherType voucherType;
}
