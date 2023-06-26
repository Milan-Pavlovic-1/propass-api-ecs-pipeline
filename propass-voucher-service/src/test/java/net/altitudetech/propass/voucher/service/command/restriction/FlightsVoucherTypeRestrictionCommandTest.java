package net.altitudetech.propass.voucher.service.command.restriction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;

public class FlightsVoucherTypeRestrictionCommandTest {
  private static final String FIELD_NAME = "flights";
  private static final String TEST_JSON = "{\"" + FIELD_NAME + "\": [123, 456]}";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private FlightDTO testFlight;
  private FlightsVoucherTypeRestrictionCommand command = new FlightsVoucherTypeRestrictionCommand();

  @BeforeEach
  public void setup() {
    this.command.setObjectMapper(MAPPER);
    this.testFlight = FlightDTO.builder().price(0.0).departureDate(LocalDateTime.now())
        .flightClass("economy").build();
  }

  @Test
  public void testApply() throws JsonMappingException, JsonProcessingException {
    this.testFlight.setId(123L);
    JsonNode entireJson = MAPPER.readTree(TEST_JSON);
    boolean valid = this.command.apply(true, entireJson.get(FIELD_NAME), this.testFlight);
    assertTrue(valid);
  }

  @Test
  public void testApplyWrongType() throws JsonMappingException, JsonProcessingException {
    testApplySameResult("{\"" + FIELD_NAME + "\": 123}", this.testFlight);
  }
  
  @Test
  public void testApplyNoFlights() throws JsonMappingException, JsonProcessingException {
    testApplySameResult("{\"buses\": [123]}", this.testFlight);
  }
  
  @Test
  public void testApplyNullFlight() throws JsonMappingException, JsonProcessingException {
    testApplySameResult(TEST_JSON, null);
  }
  
  private void testApplySameResult(String json, FlightDTO flight) throws JsonMappingException, JsonProcessingException {
    JsonNode entireJson = MAPPER.readTree(json);
    boolean valid = this.command.apply(true, entireJson.get(FIELD_NAME), flight);
    assertTrue(valid);
    valid = this.command.apply(false, entireJson.get(FIELD_NAME), flight);
    assertFalse(valid);
  }

  @Test
  public void testApplyWrongFlight() throws JsonMappingException, JsonProcessingException {
    this.testFlight.setId(789L);
    JsonNode entireJson = MAPPER.readTree(TEST_JSON);
    boolean valid = this.command.apply(true, entireJson.get(FIELD_NAME), this.testFlight);
    assertFalse(valid);
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
