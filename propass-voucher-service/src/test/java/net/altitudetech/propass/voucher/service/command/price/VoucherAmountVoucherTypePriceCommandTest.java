package net.altitudetech.propass.voucher.service.command.price;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VoucherAmountVoucherTypePriceCommandTest {
  private static final String FIELD_NAME = "vouchers";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private VoucherAmountVoucherTypePriceCommand command = new VoucherAmountVoucherTypePriceCommand();
  
  @Test
  public void testApply() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": true}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(1000.0, price);
  }
  
  @Test
  public void testApplyWithoutMultiply() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": false}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(100.0, price);
  }
  
  @Test
  public void testApplyWrongType() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"" + FIELD_NAME + "\": \"ten\"}");
    double price = this.command.apply(100.0, 10, entireJson.get(FIELD_NAME));
    assertEquals(100.0, price);
  }
  
  @Test
  public void testApplyNullNode() throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree("{\"count\": true}");
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
    assertEquals(0, this.command.getPriority());
  }
  
}
