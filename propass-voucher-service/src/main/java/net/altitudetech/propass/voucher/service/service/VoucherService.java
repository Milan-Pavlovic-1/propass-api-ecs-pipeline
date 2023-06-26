package net.altitudetech.propass.voucher.service.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.commons.service.BaseService;
import net.altitudetech.propass.voucher.service.model.Voucher;
import net.altitudetech.propass.voucher.service.repository.VoucherRepository;

@Service
public class VoucherService extends BaseService<Voucher, VoucherRepository> {
  public VoucherService(VoucherRepository repository) {
    super(repository);
  }
  
  @Override
  public List<Voucher> findAll() {
    return repository.findAllByCreatedBy(getCreatedBy());
  }
  
  @Override
  public Page<Voucher> findAll(Pageable pageable) {
    return repository.findAllByCreatedBy(getCreatedBy(), pageable);
  }
  
  private String getCreatedBy() {
    return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
