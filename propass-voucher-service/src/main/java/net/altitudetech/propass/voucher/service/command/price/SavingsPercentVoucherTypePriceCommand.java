package net.altitudetech.propass.voucher.service.command.price;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class SavingsPercentVoucherTypePriceCommand extends VoucherTypePriceCommand {

  SavingsPercentVoucherTypePriceCommand() {
    super(100);
  }

  @Override
  public Double calculate(Double price, Integer amount, JsonNode savingsJsonNode) {
    int savings = savingsJsonNode.asInt();
    return price * (100 - savings) / 100.0;
  }

  @Override
  protected boolean checkNodeType(JsonNode savingsJsonNode) {
    return savingsJsonNode.canConvertToInt();
  }

  @Override
  public boolean accept(String name) {
    return "savingsPercent".equals(name);
  }

}
