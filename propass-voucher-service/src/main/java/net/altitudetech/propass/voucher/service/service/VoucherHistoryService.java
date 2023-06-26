package net.altitudetech.propass.voucher.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.commons.service.BaseService;
import net.altitudetech.propass.voucher.service.model.VoucherHistory;
import net.altitudetech.propass.voucher.service.repository.VoucherHistoryRepository;

@Service
public class VoucherHistoryService extends BaseService<VoucherHistory, VoucherHistoryRepository> {

  public VoucherHistoryService(VoucherHistoryRepository repository) {
    super(repository);
  }
  
  public Page<VoucherHistory> findAllByVoucherId(Long voucherId, Pageable pageable) {
    return this.repository.findAllByVoucherId(voucherId, pageable);
  }
}
