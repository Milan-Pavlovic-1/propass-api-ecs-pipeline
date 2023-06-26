package net.altitudetech.propass.api.gateway.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.api.gateway.dto.FlightDetailsDTO;
import net.altitudetech.propass.api.gateway.dto.ValidationResponseDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherHistoryDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherTypeDTO;
import net.altitudetech.propass.api.gateway.service.PropassVoucherService;
import net.altitudetech.propass.commons.security.annotation.Public;

@RestController
@RequestMapping("/propass/vouchers")
public class PropassVoucherController {
  @Autowired
  private PropassVoucherService voucherService;

  @GetMapping("/{voucherId}")
  @SecurityRequirement(name = "BearerAuth")
  public VoucherDTO single(@PathVariable Long voucherId) {
    return this.voucherService.findOne(voucherId);
  }

  @PostMapping("/types/{typeId}/purchase")
  @SecurityRequirement(name = "BearerAuth")
  public VoucherDTO purchase(@PathVariable Long typeId, @RequestBody FlightDetailsDTO flight) {
    return this.voucherService.purchase(typeId, flight);
  }

  @PostMapping("/{voucherId}/redeem")
  @SecurityRequirement(name = "BearerAuth")
  public VoucherHistoryDTO redeem(@PathVariable Long voucherId, @RequestBody FlightDetailsDTO flight) {
    return this.voucherService.redeem(voucherId, flight);
  }

  @GetMapping
  @SecurityRequirement(name = "BearerAuth")
  public Page<VoucherDTO> all(Pageable pageable) {
    return this.voucherService.findAll(pageable);
  }

  @GetMapping("/{voucherId}/history")
  @SecurityRequirement(name = "BearerAuth")
  public Page<VoucherHistoryDTO> getHistory(@PathVariable Long voucherId, Pageable pageable) {
    return this.voucherService.getHistory(voucherId, pageable);
  }

  @Public
  @PostMapping("/types")
  public List<VoucherTypeDTO> allTypes(@RequestBody(required = true) FlightDetailsDTO flight) {
    return this.voucherService.findAllTypes(flight);
  }

  @PostMapping("/types/{typeId}/validate")
  @SecurityRequirement(name = "BearerAuth")
  public ValidationResponseDTO validate(@PathVariable Long typeId,
      @RequestBody(required = true) FlightDetailsDTO flight) {
    return this.voucherService.validate(typeId, flight);
  }

}
