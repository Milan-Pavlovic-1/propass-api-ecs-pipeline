package net.altitudetech.propass.api.gateway.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import java.io.IOException;
import java.util.List;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.mail.MessagingException;
import net.altitudetech.propass.api.gateway.config.PropassAPIGatewayTestConfig;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.FlightDetailsDTO;
import net.altitudetech.propass.api.gateway.dto.FlightLocationDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.api.gateway.dto.ValidationResponseDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherHistoryDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherTypeDTO;
import net.altitudetech.propass.api.gateway.service.BoardingPassService;
import net.altitudetech.propass.api.gateway.service.EmailService;
import net.altitudetech.propass.commons.util.test.TokenUtils;

@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassAPIGatewayTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropassVoucherControllerIT {
  private static final Long TEST_FLIGHT_ID = 1L;
  private static final Long TEST_VOUCHER_TYPE_ID = 2L;
  private static final Long TEST_VOUCHER_ID = 3L;
  private static final Long NEW_VOUCHER_ID = 13L;
  private static final Long NEW_VOUCHER_HISTORY_ID = 14L;

  //@formatter:off
  private static final FlightDetailsDTO TEST_FLIGHT_DETAILS = FlightDetailsDTO.builder().id(TEST_FLIGHT_ID).vouchers(123).build();
  private static final VoucherTypeDTO TEST_VOUCHER_TYPE = VoucherTypeDTO.builder()
      .id(TEST_VOUCHER_TYPE_ID).voucherAmount(123).build();
  private static final VoucherDTO TEST_VOUCHER = VoucherDTO.builder()
      .id(TEST_VOUCHER_ID)
      .flightId(TEST_FLIGHT_DETAILS.getId())
      .totalAmount(TEST_VOUCHER_TYPE.getVoucherAmount()).usedAmount(0)
      .voucherType(TEST_VOUCHER_TYPE).build();
  private static final UserDTO TEST_USER = UserDTO.builder()
      .firstName("John")
      .lastName("Constantine")
      .email("john@constantine.com")
      .build();
  private static final FlightDTO TEST_FLIGHT = FlightDTO.builder()
      .id(TEST_FLIGHT_ID).airlineId(123L)
      .locationFrom(new FlightLocationDTO("Sombor, Serbia", "SOM", "City"))
      .locationTo(new FlightLocationDTO("Novi Sad, Serbia", "NOS", "City"))
      .build();
  //@formatter:on

  @LocalServerPort
  private int port;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BoardingPassService boardingPassService;

  @Autowired
  private EmailService emailService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @AfterEach
  public void reset() {
    WireMock.reset();
  }

  @Test
  public void testSingle() throws JsonProcessingException {
    stubForGetVoucher(TEST_VOUCHER);
    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .get("/propass/vouchers/" + TEST_VOUCHER_ID)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(TEST_VOUCHER_ID.intValue()))
        .body("flightId", equalTo(TEST_FLIGHT_ID.intValue()))
        .body("totalAmount", equalTo(TEST_VOUCHER.getTotalAmount()))
        .body("voucherType.id", equalTo(TEST_VOUCHER_TYPE_ID.intValue()));
    //@formatter:on
  }

  @Test
  public void testPurchase() throws JsonProcessingException {
    //@formatter:off
    stubForVoucherTypeValidate(TEST_VOUCHER_TYPE_ID, TEST_FLIGHT_DETAILS, true);
    stubForGetVoucherType(TEST_VOUCHER_TYPE);
    stubForCreateVoucher(TEST_VOUCHER, TEST_FLIGHT_DETAILS);
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/types/" + TEST_VOUCHER_TYPE_ID + "/purchase")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(NEW_VOUCHER_ID.intValue()));
    //@formatter:on
  }

  @Test
  public void testPurchaseValidationFails() throws JsonProcessingException {
    //@formatter:off
    stubForVoucherTypeValidate(TEST_VOUCHER_TYPE_ID, TEST_FLIGHT_DETAILS, false);
    stubForGetVoucherType(TEST_VOUCHER_TYPE);
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/types/" + TEST_VOUCHER_TYPE_ID + "/purchase")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.BAD_REQUEST.value());
    //@formatter:on
  }

  @Test
  public void testRedeem()
      throws DocumentException, IOException, WriterException, MessagingException {
    byte[] pdf = new byte[0];
    doReturn(pdf).when(this.boardingPassService).generateBoardingPass(eq(TEST_FLIGHT),
        eq(TEST_USER), anyString());

    //@formatter:off
    stubForGetVoucher(TEST_VOUCHER);
    stubForUpdateVoucher(TEST_VOUCHER);
    stubForCreateVoucherHistory(TEST_VOUCHER);
    stubForMe(TEST_USER);
    stubForFlight(TEST_FLIGHT);
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/" + TEST_VOUCHER_ID + "/redeem")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(NEW_VOUCHER_HISTORY_ID.intValue()));
    //@formatter:on

    verify(this.boardingPassService).generateBoardingPass(eq(TEST_FLIGHT),
        eq(TEST_USER), anyString());
    verify(this.emailService).sendBoardingPassEmail(TEST_FLIGHT, TEST_USER, pdf);
  }

  @Test
  public void testRedeemValidationFails() throws JsonProcessingException {
    //@formatter:off
    VoucherDTO voucher = VoucherDTO.builder()
      .id(TEST_VOUCHER_ID)
      .flightId(TEST_FLIGHT_DETAILS.getId())
      .totalAmount(20).usedAmount(20)
      .voucherType(TEST_VOUCHER_TYPE).build();
    
    stubForGetVoucher(voucher);
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/" + TEST_VOUCHER_ID + "/redeem")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.BAD_REQUEST.value());
    //@formatter:on
  }

  @Test
  public void testAll() throws JsonProcessingException {
    Long secondId = 123L;
    stubForGetVouchers(TEST_VOUCHER, VoucherDTO.builder().id(secondId).build());
    //@formatter:off
    List<VoucherDTO> vouchers = given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .get("/propass/vouchers?page=1&size=10&sort=id,asc&sort=flightId,desc")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", VoucherDTO.class);
    //@formatter:on

    assertNotNull(vouchers);
    assertEquals(2, vouchers.size());
    assertTrue(vouchers.stream().anyMatch(u -> u.getId().equals(TEST_VOUCHER_ID)));
    assertTrue(vouchers.stream().anyMatch(u -> u.getId().equals(secondId)));
  }

  @Test
  public void testGetHistory() throws JsonProcessingException {
    Long firstId = 123L;
    Long secondId = 456L;
    Long thirdId = 789L;
    stubForGetVoucherHistories(TEST_VOUCHER_ID, new VoucherHistoryDTO(firstId),
        new VoucherHistoryDTO(secondId), new VoucherHistoryDTO(thirdId));
    //@formatter:off
    List<VoucherHistoryDTO> histories = given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .get("/propass/vouchers/" + TEST_VOUCHER_ID + "/history?sort=id,asc")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", VoucherHistoryDTO.class);
    //@formatter:on

    assertNotNull(histories);
    assertEquals(3, histories.size());
    assertTrue(histories.stream().anyMatch(u -> u.getId().equals(firstId)));
    assertTrue(histories.stream().anyMatch(u -> u.getId().equals(secondId)));
    assertTrue(histories.stream().anyMatch(u -> u.getId().equals(thirdId)));
  }

  @Test
  public void testAllTypes() throws JsonProcessingException {
    Long firstId = 123L;
    Long secondId = 456L;
    stubForGetVoucherTypes(TEST_FLIGHT_DETAILS, new VoucherTypeDTO(firstId),
        new VoucherTypeDTO(secondId));
    //@formatter:off
    List<VoucherTypeDTO> histories = given()
      .when()
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/types")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("$.", VoucherTypeDTO.class);
    //@formatter:on

    assertNotNull(histories);
    assertEquals(2, histories.size());
    assertTrue(histories.stream().anyMatch(u -> u.getId().equals(firstId)));
    assertTrue(histories.stream().anyMatch(u -> u.getId().equals(secondId)));
  }

  @Test
  public void testValidate() throws JsonProcessingException {
    //@formatter:off
    stubForVoucherTypeValidate(TEST_VOUCHER_TYPE_ID, TEST_FLIGHT_DETAILS, true);
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .contentType(ContentType.JSON)
        .body(TEST_FLIGHT_DETAILS)
        .post("/propass/vouchers/types/" + TEST_VOUCHER_TYPE_ID + "/validate")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("valid", equalTo(true));
    //@formatter:on
  }

  private void stubForGetVouchers(VoucherDTO... vouchers) throws JsonProcessingException {
    stubFor(get(urlEqualTo("/vouchers?page=1&size=10&sort=id,asc&sort=flightId,desc"))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json").withBody(
                this.objectMapper.writeValueAsString(new PageImpl<>(Arrays.asList(vouchers))))));
  }

  private void stubForGetVoucher(VoucherDTO voucher) throws JsonProcessingException {
    stubFor(get(urlEqualTo("/vouchers/" + voucher.getId()))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(voucher))));
  }

  private void stubForCreateVoucher(VoucherDTO voucher, FlightDetailsDTO flight)
      throws JsonProcessingException {
    VoucherDTO response = new VoucherDTO(NEW_VOUCHER_ID);
    stubFor(post(urlEqualTo("/vouchers"))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .withRequestBody(
            matchingJsonPath("$.flightId", WireMock.equalTo("" + voucher.getFlightId())))
        .withRequestBody(
            matchingJsonPath("$.totalAmount", WireMock.equalTo("" + flight.getVouchers())))
        .withRequestBody(matchingJsonPath("$.voucherType.id",
            WireMock.equalTo("" + voucher.getVoucherType().getId())))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(response))));
  }

  private void stubForUpdateVoucher(VoucherDTO voucher) throws JsonProcessingException {
    VoucherDTO updated =
        VoucherDTO.builder().id(voucher.getId()).usedAmount(voucher.getUsedAmount() + 1).build();
    stubFor(put(urlEqualTo("/vouchers/" + voucher.getId()))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .withRequestBody(
            matchingJsonPath("$.usedAmount", WireMock.equalTo("" + (voucher.getUsedAmount() + 1))))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(updated))));
  }

  private void stubForGetVoucherHistories(Long voucherId, VoucherHistoryDTO... histories)
      throws JsonProcessingException {
    stubFor(get(urlEqualTo("/vouchers/" + voucherId + "/history?page=0&size=20&sort=id,asc"))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json").withBody(
                this.objectMapper.writeValueAsString(new PageImpl<>(Arrays.asList(histories))))));
  }

  private void stubForGetVoucherHistory(Long voucherId, Long id, VoucherHistoryDTO history)
      throws JsonProcessingException {
    stubFor(get(urlEqualTo("/vouchers/" + voucherId + "/history/" + id))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(history))));
  }

  private void stubForCreateVoucherHistory(VoucherDTO voucher) throws JsonProcessingException {
    VoucherHistoryDTO response = new VoucherHistoryDTO(NEW_VOUCHER_HISTORY_ID);
    stubFor(post(urlEqualTo("/vouchers/" + voucher.getId() + "/history"))
        .withHeader("Authorization", WireMock.equalTo(TokenUtils.VALID_TOKEN))
        .withRequestBody(matchingJsonPath("$.voucher.id", WireMock.equalTo("" + voucher.getId())))
        .withRequestBody(matchingJsonPath("$.voucher.usedAmount",
            WireMock.equalTo("" + (voucher.getUsedAmount() + 1))))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(response))));
  }

  private void stubForGetVoucherTypes(FlightDetailsDTO flight, VoucherTypeDTO... types)
      throws JsonProcessingException {
    stubFor(post(urlEqualTo("/vouchers/types"))
        .withRequestBody(matchingJsonPath("$.id", WireMock.equalTo("" + flight.getId())))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(Arrays.asList(types)))));
  }

  private void stubForGetVoucherType(VoucherTypeDTO type) throws JsonProcessingException {
    stubFor(get(urlEqualTo("/vouchers/types/" + type.getId())).willReturn(
        aResponse().withStatus(HttpStatus.OK.value()).withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(type))));
  }

  private void stubForVoucherTypeValidate(Long id, FlightDetailsDTO flight, boolean isValid)
      throws JsonProcessingException {
    stubFor(post(urlEqualTo("/vouchers/types/" + id + "/validate"))
        .withRequestBody(matchingJsonPath("$.id", WireMock.equalTo("" + flight.getId())))
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(new ValidationResponseDTO(isValid)))));
  }

  private void stubForFlight(FlightDTO flight) throws JsonProcessingException {
    stubFor(get(urlEqualTo("/flights/" + flight.getId())).willReturn(
        aResponse().withStatus(HttpStatus.OK.value()).withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(flight))));
  }

  private void stubForMe(UserDTO user) throws JsonProcessingException {
    stubFor(get(urlEqualTo("/auth/me")).willReturn(
        aResponse().withStatus(HttpStatus.OK.value()).withHeader("Content-Type", "application/json")
            .withBody(this.objectMapper.writeValueAsString(user))));
  }

}

