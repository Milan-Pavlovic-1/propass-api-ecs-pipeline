package net.altitudetech.propass.voucher.service.command.price;

import org.apache.commons.lang3.function.TriFunction;
import com.fasterxml.jackson.databind.JsonNode;
import net.altitudetech.propass.voucher.service.command.VoucherTypeCommand;

public abstract class VoucherTypePriceCommand extends VoucherTypeCommand
    implements TriFunction<Double, Integer, JsonNode, Double> {

  public VoucherTypePriceCommand(int priority) {
    super(priority);
  }
  
  @Override
  public final Double apply(Double price, Integer amount, JsonNode node) {
    if (node != null && checkNodeType(node)) {
      return calculate(price, amount, node);
    }
    return price;
  }
  protected abstract Double calculate(Double price, Integer amount, JsonNode node);
  protected abstract boolean checkNodeType(JsonNode node);

}
