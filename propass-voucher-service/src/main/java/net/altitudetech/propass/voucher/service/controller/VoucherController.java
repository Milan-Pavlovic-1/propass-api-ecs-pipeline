package net.altitudetech.propass.voucher.service.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.commons.controller.BaseController;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.voucher.service.dto.VoucherDTO;
import net.altitudetech.propass.voucher.service.model.Voucher;
import net.altitudetech.propass.voucher.service.service.VoucherService;

@RestController
@RequestMapping("/vouchers")
public class VoucherController extends BaseController<Voucher, VoucherDTO> {
  @Autowired
  private VoucherService voucherService;
  
  @Autowired
  public VoucherController(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @GetMapping
  public Page<VoucherDTO> list(Pageable pageable) {
    return toDTOs(this.voucherService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public VoucherDTO single(@PathVariable Long id) {
    Voucher voucher = this.voucherService.findOne(id)
        .orElseThrow(() -> new NotFoundException("Voucher " + id + " not found."));
    return toDTO(voucher);
  }
  
  @PostMapping
  public VoucherDTO create(@RequestBody VoucherDTO voucher) {
    voucher.setUsedAmount(0);
    return toDTO(this.voucherService.save(toEntity(voucher)));
  }
  
  @PutMapping("/{id}")
  public VoucherDTO update(@PathVariable Long id, @RequestBody VoucherDTO voucher) {
    voucher.setId(id);
    return toDTO(this.voucherService.save(toEntity(voucher)));
  }
}
