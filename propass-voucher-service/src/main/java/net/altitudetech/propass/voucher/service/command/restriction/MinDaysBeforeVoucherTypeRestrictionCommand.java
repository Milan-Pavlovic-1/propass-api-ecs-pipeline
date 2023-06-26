package net.altitudetech.propass.voucher.service.command.restriction;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;

@Component
public class MinDaysBeforeVoucherTypeRestrictionCommand extends VoucherTypeRestrictionCommand {

  MinDaysBeforeVoucherTypeRestrictionCommand() {
    super(10);
  }

  @Override
  public Boolean checkRestriction(Boolean valid, JsonNode minDaysBeforeJsonNode, FlightDTO flight) {
    int minDaysBefore = minDaysBeforeJsonNode.asInt();
    return valid && isBeforeDelta(minDaysBefore, flight.getDepartureDate());
  }

  @Override
  protected boolean checkNodeType(JsonNode minDaysBeforeJsonNode) {
    return minDaysBeforeJsonNode.canConvertToInt();
  }

  @Override
  public boolean accept(String name) {
    return "minDaysBefore".equals(name);
  }

  private boolean isBeforeDelta(int deltaDays, LocalDateTime date) {
    // if a flight does not have a date it means it's not for redemption but rather purchasing vouchers
    return date == null || LocalDateTime.now().plusDays(deltaDays).isBefore(date);
  }

}
