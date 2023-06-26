package net.altitudetech.propass.voucher.service.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.voucher.service.config.PropassVoucherServiceTestConfig;
import net.altitudetech.propass.voucher.service.dto.VoucherHistoryDTO;
import net.altitudetech.propass.voucher.service.model.VoucherHistory;
import net.altitudetech.propass.voucher.service.service.VoucherHistoryService;

@DirtiesContext
@Sql("/import.sql")
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassVoucherServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoucherHistoryControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private VoucherHistoryService voucherHistoryService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleVoucherHistory() {
    int voucherHistoryId = 1;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/1/history/" + voucherHistoryId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(voucherHistoryId))
        .body("voucher.id", equalTo(1))
        .body("ticketRef", equalTo("ticket-123"));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleVoucherHistoryNoAuthorizationToken() {
    testForbidden("/vouchers/1/history/1");
  }
  
  @Test
  public void testGetSingleVoucherHistoryNotFound() {
    int voucherHistoryId = 4;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/1/history" + voucherHistoryId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value());
    //@formatter:on
  }

  @Test
  public void testGetAllVoucherHistories() {
    //@formatter:off
    List<VoucherHistoryDTO> voucherHistories = given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/vouchers/1/history")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", VoucherHistoryDTO.class);
    //@formatter:on
    assertNotNull(voucherHistories);
    assertEquals(2, voucherHistories.size());
    assertTrue(voucherHistories.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(voucherHistories.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testGetAllVoucherHistoriesNoAuthorizationToken() {
    testForbidden("/vouchers/1/history");
  }
  
  @Test
  public void testCreateVoucherHistory() {
    //@formatter:off
    VoucherHistoryDTO voucherHistory = VoucherHistoryDTO.builder()
        .ticketRef("ticket-012").build();

    Long id = given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(voucherHistory)
      .when()
        .post("/vouchers/1/history")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getLong("id");
    //@formatter:on
    
    assertNotNull(id);
    
    VoucherHistory dbVoucherHistory = this.voucherHistoryService.findOne(id).get();
    
    assertNotNull(dbVoucherHistory);
    assertEquals(voucherHistory.getTicketRef(), dbVoucherHistory.getTicketRef());
    assertEquals(1L, dbVoucherHistory.getVoucher().getId());
  }
  
  @Test
  public void testCreateVoucherHistoryNoAuthorizationToken() {
    testForbidden("/vouchers/1/history", "POST");
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

