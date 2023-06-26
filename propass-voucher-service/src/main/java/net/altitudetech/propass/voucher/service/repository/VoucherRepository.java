package net.altitudetech.propass.voucher.service.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import net.altitudetech.propass.voucher.service.model.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  public List<Voucher> findAllByCreatedBy(String createdBy);
  public Page<Voucher> findAllByCreatedBy(String createdBy, Pageable pageable);
}
