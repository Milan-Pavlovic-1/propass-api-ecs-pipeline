package net.altitudetech.propass.voucher.service.controller;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.commons.controller.BaseController;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.commons.security.annotation.Public;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;
import net.altitudetech.propass.voucher.service.dto.ValidationResponseDTO;
import net.altitudetech.propass.voucher.service.dto.VoucherTypeDTO;
import net.altitudetech.propass.voucher.service.model.VoucherType;
import net.altitudetech.propass.voucher.service.service.VoucherTypeService;

@RestController
@RequestMapping("/vouchers/types")
public class VoucherTypeController extends BaseController<VoucherType, VoucherTypeDTO> {
  @Autowired
  private VoucherTypeService voucherTypeService;
  
  public VoucherTypeController(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @GetMapping("/{id}")
  public VoucherTypeDTO single(@PathVariable Long id) {
    return toDTO(this.voucherTypeService.findOne(id)
        .orElseThrow(() -> new NotFoundException("No such voucher type.")));
  }

  @Public
  @PostMapping
  public List<VoucherTypeDTO> all(@RequestBody(required = true) FlightDTO flight) {
    return this.voucherTypeService.findAllForFlight(flight);
  }

  @PostMapping("/{id}/validate")
  public ValidationResponseDTO validate(@PathVariable Long id,
      @RequestBody(required = true) FlightDTO flight) {
    return this.voucherTypeService.validate(flight, id);
  }

}
