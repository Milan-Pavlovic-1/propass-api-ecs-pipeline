package net.altitudetech.propass.flight.service.controller;

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
import net.altitudetech.propass.flight.service.config.PropassFlightServiceTestConfig;
import net.altitudetech.propass.flight.service.dto.FlightDTO;
import net.altitudetech.propass.flight.service.dto.FlightLocationDTO;
import net.altitudetech.propass.flight.service.model.Flight;
import net.altitudetech.propass.flight.service.service.FlightService;

@DirtiesContext
@Sql("/import.sql")
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassFlightServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private FlightService flightService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleFlight() {
    int flightId = 1;

    //@formatter:off
    given()
      .when()
        .get("/flights/" + flightId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(flightId))
        .body("airlineId", equalTo(1))
        .body("locationFrom.name", equalTo("Amman, Jordan"))
        .body("locationFrom.code", equalTo("AMM"))
        .body("locationFrom.type", equalTo("Airport"))
        .body("locationTo.name", equalTo("Muscat, Oman"))
        .body("locationTo.code", equalTo("MCT"))
        .body("locationTo.type", equalTo("Airport"));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleFlightNotFound() {
    int flightId = 4;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/flights/" + flightId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value());
    //@formatter:on
  }

  @Test
  public void testGetAllFlights() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(3, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(2L)));
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByFromCode() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?locationFrom.code=AMM")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(2, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(2L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByFromName() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?locationFrom.name=amm")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(2, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(2L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByToCode() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?locationTo.code=MCT")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(2, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByToName() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?locationTo.name=Ca")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(1, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(2L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByToCodeAndFromCode() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?locationFrom.code=AMM&locationTo.code=MCT")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(1, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(1L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByAirlineId() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?airlineId=2")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(1, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testGetAllFlightsFilteredByAll() {
    //@formatter:off
    List<FlightDTO> flights = given()
      .when()
        .get("/flights?airlineId=2&locationFrom.code=CAI&locationFrom.name=CAIR&locationTo.code=MCT&locationTo.name=mus")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", FlightDTO.class);
    //@formatter:on
    assertNotNull(flights);
    assertEquals(1, flights.size());
    assertTrue(flights.stream().anyMatch(u -> u.getId().equals(3L)));
  }
  
  @Test
  public void testCreateFlight() {
    //@formatter:off
    FlightDTO flight = FlightDTO.builder()
        .airlineId(1L)
        .locationFrom(new FlightLocationDTO("Sombor, Serbia", "SOM", "City"))
        .locationTo(new FlightLocationDTO("Novi Sad, Serbia", "NOS", "City"))
        .build();

    Long id = given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(flight)
      .when()
        .post("/flights")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getLong("id");
    //@formatter:on
    
    assertNotNull(id);
    
    Flight dbFlight = this.flightService.findOne(id).get();
    
    assertNotNull(dbFlight);
    assertEquals(flight.getAirlineId(), dbFlight.getAirlineId());
    assertEquals(flight.getLocationFrom().getName(), dbFlight.getLocationFrom().getName());
    assertEquals(flight.getLocationFrom().getCode(), dbFlight.getLocationFrom().getCode());
    assertEquals(flight.getLocationFrom().getType(), dbFlight.getLocationFrom().getType());
    assertEquals(flight.getLocationTo().getName(), dbFlight.getLocationTo().getName());
    assertEquals(flight.getLocationTo().getCode(), dbFlight.getLocationTo().getCode());
    assertEquals(flight.getLocationTo().getType(), dbFlight.getLocationTo().getType());
    assertEquals("john@constantine.com", dbFlight.getCreatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbFlight.getCreatedAt()));
    assertEquals("john@constantine.com", dbFlight.getUpdatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbFlight.getUpdatedAt()));
  }
  
  @Test
  public void testCreateFlightNoAuthorizationToken() {
    testForbidden("/flights", "POST");
  }
  
  @Test
  public void testUpdateFlight() {
    Long flightId = 1L;
    
    //@formatter:off
    FlightDTO flight = FlightDTO.builder()
        .airlineId(1L)
        .locationFrom(new FlightLocationDTO("Sombor, Serbia", "SOM", "City"))
        .locationTo(new FlightLocationDTO("Novi Sad, Serbia", "NOS", "City"))
        .build();

    given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(flight)
      .when()
        .put("/flights/" + flightId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(flightId.intValue()));
    //@formatter:on
    
    Flight dbFlight = this.flightService.findOne(flightId).get();
    
    assertNotNull(dbFlight);
    assertEquals(flight.getAirlineId(), dbFlight.getAirlineId());
    assertEquals(flight.getLocationFrom().getName(), dbFlight.getLocationFrom().getName());
    assertEquals(flight.getLocationFrom().getCode(), dbFlight.getLocationFrom().getCode());
    assertEquals(flight.getLocationFrom().getType(), dbFlight.getLocationFrom().getType());
    assertEquals(flight.getLocationTo().getName(), dbFlight.getLocationTo().getName());
    assertEquals(flight.getLocationTo().getCode(), dbFlight.getLocationTo().getCode());
    assertEquals(flight.getLocationTo().getType(), dbFlight.getLocationTo().getType());
    assertEquals("john@constantine.com", dbFlight.getUpdatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbFlight.getUpdatedAt()));
  }
  
  @Test
  public void testUpdateFlightNoAuthorizationToken() {
    testForbidden("/flights/1", "PUT");
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

