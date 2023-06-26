package net.altitudetech.propass.voucher.service.command.price;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class VoucherAmountVoucherTypePriceCommand extends VoucherTypePriceCommand {

  VoucherAmountVoucherTypePriceCommand() {
    super(0);
  }

  @Override
  public Double calculate(Double price, Integer amount, JsonNode vouchersJsonNode) {
    boolean shouldMultiply = vouchersJsonNode.asBoolean();
    return shouldMultiply ? price * amount : price;
  }

  @Override
  protected boolean checkNodeType(JsonNode amountJsonNode) {
    return amountJsonNode.isBoolean();
  }

  @Override
  public boolean accept(String name) {
    return "vouchers".equals(name);
  }

}
