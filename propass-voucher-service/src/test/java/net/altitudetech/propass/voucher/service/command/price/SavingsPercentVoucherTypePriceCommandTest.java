package net.altitudetech.propass.voucher.service.command.price;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SavingsPercentVoucherTypePriceCommandTest {
  private static final String FIELD_NAME = "savingsPercent";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private SavingsPercentVoucherTypePriceCommand command = new SavingsPercentVoucherTypePriceCommand();
  
  @Test
  public void testApply() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": 10}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(90.0, price);
  }
  
  @Test
  public void testApplyWrongType() throws JsonMappingException, JsonProcessingException {
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
