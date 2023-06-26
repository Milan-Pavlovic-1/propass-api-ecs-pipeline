package net.altitudetech.propass.voucher.service.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.commons.controller.BaseController;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.voucher.service.dto.VoucherDTO;
import net.altitudetech.propass.voucher.service.dto.VoucherHistoryDTO;
import net.altitudetech.propass.voucher.service.model.VoucherHistory;
import net.altitudetech.propass.voucher.service.service.VoucherHistoryService;

@RestController
@RequestMapping("/vouchers/{voucherId}/history")
public class VoucherHistoryController extends BaseController<VoucherHistory, VoucherHistoryDTO> {
  @Autowired
  private VoucherHistoryService voucherHistoryService;

  @Autowired
  public VoucherHistoryController(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @GetMapping
  public Page<VoucherHistoryDTO> list(@PathVariable Long voucherId, Pageable pageable) {
    return toDTOs(this.voucherHistoryService.findAllByVoucherId(voucherId, pageable));
  }

  @GetMapping("/{id}")
  public VoucherHistoryDTO single(@PathVariable Long voucherId, @PathVariable Long id) {
    VoucherHistory history = this.voucherHistoryService.findOne(id)
        .orElseThrow(() -> new NotFoundException("VoucherHistory " + id + " not found."));
    return toDTO(history);
  }

  @PostMapping
  public VoucherHistoryDTO create(@PathVariable Long voucherId, @RequestBody VoucherHistoryDTO history) {
    history.setVoucher(new VoucherDTO(voucherId));
    return toDTO(this.voucherHistoryService.save(toEntity(history)));
  }

}
