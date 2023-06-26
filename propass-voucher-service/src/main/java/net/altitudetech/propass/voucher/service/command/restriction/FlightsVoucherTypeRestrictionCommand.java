package net.altitudetech.propass.voucher.service.command.restriction;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;

@Slf4j
@Component
public class FlightsVoucherTypeRestrictionCommand extends VoucherTypeRestrictionCommand {

  @Autowired
  private ObjectMapper objectMapper;

  FlightsVoucherTypeRestrictionCommand() {
    super(0);
  }

  @Override
  public Boolean checkRestriction(Boolean valid, JsonNode flightsJsonNode, FlightDTO flight) {
      try {
        List<Long> ids = this.objectMapper.readerForListOf(Long.class).readValue(flightsJsonNode);
        return valid && ids.contains(flight.getId());
      } catch (IOException e) {
        log.error("Encountered error while converting list of flight ids.", e);
      }
      return valid;
  }
  
  @Override
  protected boolean checkNodeType(JsonNode flightsJsonNode) {
    return flightsJsonNode.isArray();
  }

  @Override
  public boolean accept(String name) {
    return "flights".equals(name);
  }

  // package visibility for tests
  void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
  
}
