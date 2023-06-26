package net.altitudetech.propass.voucher.service.command.restriction;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;

@Component
public class ClassVoucherTypeRestrictionCommand extends VoucherTypeRestrictionCommand {

  ClassVoucherTypeRestrictionCommand() {
    super(10);
  }

  @Override
  public Boolean checkRestriction(Boolean valid, JsonNode classJsonNode, FlightDTO flight) {
    String flightClass = classJsonNode.asText();
    return valid && flightClass.equals(flight.getFlightClass());
  }
  
  @Override
  protected boolean checkNodeType(JsonNode classJsonNode) {
    return classJsonNode.isTextual();
  }

  @Override
  public boolean accept(String name) {
    return "class".equals(name);
  }

}
