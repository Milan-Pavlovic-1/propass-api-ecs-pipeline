package net.altitudetech.propass.voucher.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import net.altitudetech.propass.voucher.service.model.VoucherType;

public interface VoucherTypeRepository extends JpaRepository<VoucherType, Long> {
}
