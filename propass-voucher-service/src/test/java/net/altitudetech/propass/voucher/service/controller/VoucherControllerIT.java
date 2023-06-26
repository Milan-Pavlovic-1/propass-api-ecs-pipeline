package net.altitudetech.propass.voucher.service.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.voucher.service.config.PropassVoucherServiceTestConfig;
import net.altitudetech.propass.voucher.service.dto.VoucherDTO;
import net.altitudetech.propass.voucher.service.dto.VoucherTypeDTO;
import net.altitudetech.propass.voucher.service.model.Voucher;
import net.altitudetech.propass.voucher.service.service.VoucherService;

@DirtiesContext
@Sql("/import.sql")
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassVoucherServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoucherControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private VoucherService voucherService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleVoucher() {
    int voucherId = 1;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/" + voucherId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(voucherId))
        .body("totalAmount", equalTo(10))
        .body("usedAmount", equalTo(2))
        .body("flightId", equalTo(123))
        .body("voucherType.id", equalTo(1));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleVoucherNoAuthorizationToken() {
    testForbidden("/vouchers/1");
  }
  
  @Test
  public void testGetSingleVoucherNotFound() {
    int voucherId = 4;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/" + voucherId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value());
    //@formatter:on
  }

  @Test
  public void testGetAllVouchers() {
    //@formatter:off
    List<VoucherDTO> vouchers = given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", VoucherDTO.class);
    //@formatter:on
    assertNotNull(vouchers);
    assertEquals(2, vouchers.size());
    assertTrue(vouchers.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(vouchers.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testGetAllVouchersNoAuthorizationToken() {
    testForbidden("/vouchers");
  }
  
  @Test
  public void testCreateVoucher() {
    //@formatter:off
    VoucherDTO voucher = VoucherDTO.builder()
        .totalAmount(20)
        .voucherType(VoucherTypeDTO.builder().id(2L).build())
        .flightId(789L).build();

    Long id = given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(voucher)
      .when()
        .post("/vouchers")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getLong("id");
    //@formatter:on
    
    assertNotNull(id);
    
    Voucher dbVoucher = this.voucherService.findOne(id).get();
    
    assertNotNull(dbVoucher);
    assertEquals(voucher.getTotalAmount(), dbVoucher.getTotalAmount());
    assertEquals(0, dbVoucher.getUsedAmount());
    assertEquals(voucher.getFlightId(), dbVoucher.getFlightId());
    assertEquals("john@constantine.com", dbVoucher.getCreatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbVoucher.getCreatedAt()));
    assertEquals("john@constantine.com", dbVoucher.getUpdatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbVoucher.getUpdatedAt()));
    assertEquals(voucher.getVoucherType().getId(), dbVoucher.getVoucherType().getId());
  }
  
  @Test
  public void testCreateVoucherNoAuthorizationToken() {
    testForbidden("/vouchers", "POST");
  }
  
  @Test
  public void testUpdateVoucher() {
    Long voucherId = 1L;
    
    //@formatter:off
    VoucherDTO voucher = VoucherDTO.builder()
        .totalAmount(20)
        .usedAmount(10)
        .flightId(789L).build();

    given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(voucher)
      .when()
        .put("/vouchers/" + voucherId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(voucherId.intValue()));
    //@formatter:on
    
    Voucher dbVoucher = this.voucherService.findOne(voucherId).get();
    
    assertNotNull(dbVoucher);
    assertEquals(10, dbVoucher.getTotalAmount());
    assertEquals(voucher.getUsedAmount(), dbVoucher.getUsedAmount());
    assertEquals(123, dbVoucher.getFlightId());
    assertEquals("john@constantine.com", dbVoucher.getUpdatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbVoucher.getUpdatedAt()));
  }
  
  @Test
  public void testUpdateVoucherNoAuthorizationToken() {
    testForbidden("/vouchers/1", "PUT");
  }
  
  private void testForbidden(String url) {
    testForbidden(url, "GET");
  }
  
  private void testForbidden(String url, String method) {
    //@formatter:off
    RequestSpecification spec = given()
      .when();
    Response resp = null;
    
    switch (method) {
      case "POST": resp = spec.post(url); break;
      case "PUT": resp = spec.put(url); break;
      case "DELETE": resp = spec.delete(url); break;
      default: resp = spec.get(url); break;
    }
    
    resp.then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }

}

