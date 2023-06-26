package net.altitudetech.propass.voucher.service.command.price;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SavingsFormulaVoucherTypePriceCommand extends VoucherTypePriceCommand {

  SavingsFormulaVoucherTypePriceCommand() {
    super(100);
  }

  @Override
  public Double calculate(Double price, Integer amount, JsonNode savingsJsonNode) {
    String formula = savingsJsonNode.asText();
    return price * (100 - evaluate(formula, amount)) / 100.0;
  }

  private Double evaluate(String formula, Integer amount) {
    Engine engine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();
    Context ctx = Context.newBuilder("js").engine(engine).build();
    try {
      // set the variable
      ctx.eval("js", "v=" + amount + ";");
      Object result = ctx.eval("js", formula);
      return Double.valueOf(result.toString());
    } catch (Exception e) {
      log.error("Error evaluating formula: " + formula, e);
      return 0.0;
    }
  }

  @Override
  protected boolean checkNodeType(JsonNode savingsJsonNode) {
    return savingsJsonNode.isTextual();
  }

  @Override
  public boolean accept(String name) {
    return "savingsFormula".equals(name);
  }

}
