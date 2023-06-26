package net.altitudetech.propass.voucher.service.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.commons.service.BaseService;
import net.altitudetech.propass.voucher.service.command.price.VoucherTypePriceCommand;
import net.altitudetech.propass.voucher.service.command.restriction.VoucherTypeRestrictionCommand;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;
import net.altitudetech.propass.voucher.service.dto.ValidationResponseDTO;
import net.altitudetech.propass.voucher.service.dto.VoucherTypeDTO;
import net.altitudetech.propass.voucher.service.model.VoucherType;
import net.altitudetech.propass.voucher.service.repository.VoucherTypeRepository;

@Service
public class VoucherTypeService extends BaseService<VoucherType, VoucherTypeRepository> {

  @Autowired
  private List<VoucherTypePriceCommand> priceCommands;

  @Autowired
  private List<VoucherTypeRestrictionCommand> restrictionCommands;

  public VoucherTypeService(VoucherTypeRepository repository) {
    super(repository);
  }

  public List<VoucherTypeDTO> findAllForFlight(FlightDTO flight) {
    List<VoucherTypeDTO> types = new ArrayList<>();
    for (VoucherType type : findAll()) {
      if (validate(flight, type).getValid()) {
        VoucherTypeDTO dto = map(flight, type);
        types.add(dto);
      }
    }
    // TODO filter on airline
    return types;
  }

  public ValidationResponseDTO validate(FlightDTO flight, Long voucherTypeId) {
    return validate(flight, findOne(voucherTypeId)
        .orElseThrow(() -> new NotFoundException("Unknown voucher type " + voucherTypeId)));
  }

  private Double calculatePrice(FlightDTO flight, VoucherType voucherType) {
    Double price = 0.0;
    if (flight != null) {
      price = flight.getPrice();
      if (voucherType.getPriceFormula() != null) {
        Iterator<Entry<String, JsonNode>> fields = voucherType.getPriceFormula().fields();

        while (fields.hasNext()) {
          price = calculateForField(price, flight.getVouchers(), fields.next());
        }
      }
    }
    return price;
  }

  private Double calculateForField(Double price, Integer amount, Entry<String, JsonNode> field) {
    for (VoucherTypePriceCommand formula : this.priceCommands) {
      if (formula.accept(field.getKey())) {
        price = formula.apply(price, amount, field.getValue());
      }
    }
    return price;
  }

  private ValidationResponseDTO validate(FlightDTO flight, VoucherType voucherType) {
    boolean valid = true;
    if (voucherType.getRestrictionFormula() != null) {
      Iterator<Entry<String, JsonNode>> fields = voucherType.getRestrictionFormula().fields();

      while (fields.hasNext()) {
        valid = isValidForField(valid, fields.next(), flight);
      }
    }
    return new ValidationResponseDTO(valid);
  }

  private boolean isValidForField(boolean valid, Entry<String, JsonNode> field, FlightDTO flight) {
    for (VoucherTypeRestrictionCommand restriction : this.restrictionCommands) {
      if (restriction.accept(field.getKey())) {
        valid = restriction.apply(valid, field.getValue(), flight);
      }
    }
    return valid;
  }

  private VoucherTypeDTO map(VoucherType type) {
    return map(null, type);
  }

  private VoucherTypeDTO map(FlightDTO flight, VoucherType type) {
    VoucherTypeDTO dto = VoucherTypeDTO.builder().id(type.getId()).name(type.getName())
        .priceFormula(type.getPriceFormula()).restrictionFormula(type.getRestrictionFormula())
        .price(calculatePrice(flight, type)).build();
    return dto;
  }
}
