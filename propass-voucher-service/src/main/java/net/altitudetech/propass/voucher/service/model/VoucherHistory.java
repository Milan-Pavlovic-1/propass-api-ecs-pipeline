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
import net.altitudetech.propass.commons.model.BaseModel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vouchers_history")
public class VoucherHistory extends BaseModel {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "voucher_id",
      foreignKey = @ForeignKey(name = "vouchers_history_voucher_id_fk"), updatable = false)
  private Voucher voucher;
  @Column(nullable = false, updatable = false)
  private String ticketRef;
}
