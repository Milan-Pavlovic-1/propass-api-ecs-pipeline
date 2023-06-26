package net.altitudetech.propass.voucher.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import net.altitudetech.propass.voucher.service.model.VoucherHistory;

public interface VoucherHistoryRepository extends JpaRepository<VoucherHistory, Long> {
  Page<VoucherHistory> findAllByVoucherId(Long voucherId, Pageable pageable);
}
