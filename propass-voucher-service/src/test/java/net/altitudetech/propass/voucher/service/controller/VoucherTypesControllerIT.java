package net.altitudetech.propass.voucher.service.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.voucher.service.config.PropassVoucherServiceTestConfig;
import net.altitudetech.propass.voucher.service.dto.FlightDTO;
import net.altitudetech.propass.voucher.service.dto.VoucherTypeDTO;
import net.altitudetech.propass.voucher.service.model.VoucherType;
import net.altitudetech.propass.voucher.service.service.VoucherTypeService;

@DirtiesContext
@Sql("/import.sql")
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassVoucherServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoucherTypesControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private VoucherTypeService voucherTypeService;
  
  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleVoucherType() {
    int voucherTypeId = 1;

    //@formatter:off
    VoucherTypeDTO voucherType = given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/types/" + voucherTypeId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(voucherTypeId))
        .extract().body().as(VoucherTypeDTO.class);
    //@formatter:on
    
    VoucherType voucherTypeDB = this.voucherTypeService.findOne(1L).get();
    assertEquals(voucherTypeDB.getName(), voucherType.getName());
    assertEquals(voucherTypeDB.getPriceFormula(), voucherType.getPriceFormula());
    assertEquals(voucherTypeDB.getRestrictionFormula(), voucherType.getRestrictionFormula());
  }
  
  @Test
  public void testGetAllForFlight() {
    //@formatter:off
    FlightDTO flight = FlightDTO.builder()
        .id(456L)
        .flightClass("economy")
        .price(100.0)
        .vouchers(10).build();

    List<VoucherTypeDTO> voucherTypes = given()
      .contentType(ContentType.JSON)
      .body(flight)
      .when()
        .post("/vouchers/types")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("$.", VoucherTypeDTO.class);
    //@formatter:on
    
    assertNotNull(voucherTypes);
    assertEquals(2, voucherTypes.size());
    assertTrue(voucherTypes.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(voucherTypes.stream().anyMatch(u -> u.getId().equals(3L)));
    
    VoucherTypeDTO first, third;
    
    if (voucherTypes.get(0).getId().equals(1L)) {
      first = voucherTypes.get(0);
      third = voucherTypes.get(1);
    } else {
      first = voucherTypes.get(1);
      third = voucherTypes.get(0);
    }
    
    VoucherType firstDB = this.voucherTypeService.findOne(1L).get();
    VoucherType thirdDB = this.voucherTypeService.findOne(3L).get();
    
    assertEquals(firstDB.getName(), first.getName());
    assertEquals(firstDB.getPriceFormula(), first.getPriceFormula());
    assertEquals(firstDB.getRestrictionFormula(), first.getRestrictionFormula());
    assertEquals(800.0, first.getPrice());
    
    assertEquals(thirdDB.getName(), third.getName());
    assertEquals(thirdDB.getPriceFormula(), third.getPriceFormula());
    assertEquals(thirdDB.getRestrictionFormula(), third.getRestrictionFormula());
    assertEquals(1000.0, third.getPrice());
  }

  @Test
  public void testValidate() {
    testValidation(456L, "economy", LocalDateTime.now().plusDays(60), true);
  }
  
  @Test
  public void testValidateInvalidClass() {
    testValidation(456L, "first", LocalDateTime.now().plusDays(60), false);
  }
  
  @Test
  public void testValidateInvalidFlight() {
    testValidation(789L, "economy", LocalDateTime.now().plusDays(60), false);
  }
  
  @Test
  public void testValidateInvalidDate() {
    testValidation(456L, "economy", LocalDateTime.now().plusDays(5), false);
  }
  
  private void testValidation(Long flightId, String flightClass, LocalDateTime departureDate, Boolean result) {
    //@formatter:off
    FlightDTO flight = FlightDTO.builder()
        .id(flightId)
        .flightClass(flightClass)
        .departureDate(departureDate).build();

    given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(flight)
      .when()
        .post("/vouchers/types/1/validate")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("valid", equalTo(result));
    //@formatter:on
  }
}

