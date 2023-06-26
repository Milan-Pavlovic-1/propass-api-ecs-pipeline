package net.altitudetech.propass.voucher.service.command.price;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SavingsFormulaVoucherTypePriceCommandTest {
  private static final String FIELD_NAME = "savingsFormula";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private SavingsFormulaVoucherTypePriceCommand command = new SavingsFormulaVoucherTypePriceCommand();
  
  @Test
  public void testApply() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": \"v < 10 ? 8 : v < 20 ? 9 : v < 30 ? 10 : 11\"}");
    double price = this.command.apply(100.0, 17, entireJson.get(FIELD_NAME));
    assertEquals(91.0, price);
  }
  
  @Test
  public void testApplyWrongType() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": 10}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(100.0, price);
  }
  
  @Test
  public void testApplyNotAProperFormula() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": \"ten\"}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(100.0, price);
  }
  
  @Test
  public void testApplyNullNode() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"savingsAmount\": 10}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(100.0, price);
  }
  
  @Test
  public void testAccept() {
    assertTrue(this.command.accept(FIELD_NAME));
  }
  
  @Test
  public void testAcceptRandomName() {
    assertFalse(this.command.accept("random"));
  }
  
  @Test
  public void testPriority() {
    assertEquals(100, this.command.getPriority());
  }
  
}
