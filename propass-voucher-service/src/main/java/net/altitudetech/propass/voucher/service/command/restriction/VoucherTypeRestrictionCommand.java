package net.altitudetech.propass.voucher.service.command.restriction;

import org.apache.commons.lang3.function.TriFunction;
import com.fasterxml.jackson.databind.JsonNode;
import net.altitudetech.propass.voucher.service.command.VoucherTypeCommand;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;

public abstract class VoucherTypeRestrictionCommand extends VoucherTypeCommand
    implements TriFunction<Boolean, JsonNode, FlightDTO, Boolean> {

  public VoucherTypeRestrictionCommand(int priority) {
    super(priority);
  }

  @Override
  public final Boolean apply(Boolean valid, JsonNode node, FlightDTO flight) {
    if (node != null && flight != null && checkNodeType(node)) {
      return checkRestriction(valid, node, flight);
    }
    return valid;
  }
  protected abstract Boolean checkRestriction(Boolean valid, JsonNode node, FlightDTO flight);
  protected abstract boolean checkNodeType(JsonNode node);
  
}
