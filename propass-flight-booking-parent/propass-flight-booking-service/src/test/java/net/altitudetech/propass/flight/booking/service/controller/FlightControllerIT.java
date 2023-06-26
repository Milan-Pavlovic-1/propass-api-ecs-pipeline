package net.altitudetech.propass.flight.booking.service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.flight.booking.service.config.PropassFlightBookingTestConfig;
import net.altitudetech.propass.flight.booking.service.dto.Arrival;
import net.altitudetech.propass.flight.booking.service.dto.Departure;
import net.altitudetech.propass.flight.booking.service.dto.Flight;
import net.altitudetech.propass.flight.booking.service.dto.FlightResponseDTO;
import net.altitudetech.propass.flight.booking.service.dto.FlightSegment;
import net.altitudetech.propass.flight.booking.service.dto.Passenger;
import net.altitudetech.propass.flight.booking.service.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.service.dto.SearchFlightResponseDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassFlightBookingTestConfig.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightControllerIT {
  
  @LocalServerPort
  private int port;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  
  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }
  
  @Test
  public void testSearchFlightsNoAuthorizationToken() {
    SearchFlightRequestDTO request = getSearchRequestBuilder().build();
    //@formatter:off
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/flights/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  @Test
  public void testSearchFlightsOneWay() throws JsonProcessingException {
    String departureDate = "2023-05-10T00:00:00";
    String fromLocation = "MCT";
    String toLocation = "WAS";
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .roundTrip(false)
      .departureDateTime(departureDate)
      .originLocationCode(fromLocation)
      .destinationLocationCode(toLocation)
      .build();
    
    SearchFlightResponseDTO mockedResponse = SearchFlightResponseDTO.builder()
      .outboundFlight(getMockedFlight("MCT", "MCT", "IAD", "WAS"))
      .returnFlights(Collections.emptyList())
      .build();
    //@formatter:off
    stubForSearchFlights(mockedResponse, fromLocation, toLocation, departureDate, null, false);
    
    SearchFlightResponseDTO response = given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/flights/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
      .extract().body().as(SearchFlightResponseDTO.class);
    
    assertNotNull(response.getOutboundFlights());
    assertTrue(response.getReturnFlights().isEmpty());
    assertEquals(1, response.getOutboundFlights().size());
    
    assertFlight(
      response.getOutboundFlights().get(0),
      "MCT",
      "MCT",
      "IAD",
      "WAS"
    );
    //@formatter:on
  }
  
  @Test
  public void testSearchFlightsRoundTrip() throws JsonProcessingException {
    String departureDate = "2023-05-10T00:00:00";
    String returnDate = "2023-05-10T00:00:00";
    String fromLocation = "MCT";
    String toLocation = "WAS";
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .roundTrip(true)
      .departureDateTime(departureDate)
      .returnDateTime(returnDate)
      .originLocationCode(fromLocation)
      .destinationLocationCode(toLocation)
      .build();
    
    SearchFlightResponseDTO mockedResponse = SearchFlightResponseDTO.builder()
      .outboundFlight(getMockedFlight("MCT", "MCT", "IAD", "WAS"))
      .returnFlight(getMockedFlight("IAD", "WAS", "MCT", "MCT"))
      .build();
    
    //@formatter:off
    stubForSearchFlights(mockedResponse, fromLocation, toLocation, departureDate, returnDate,true);
    
    SearchFlightResponseDTO response = given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/flights/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().as(SearchFlightResponseDTO.class);

    assertNotNull(response.getOutboundFlights());
    assertEquals(1, response.getOutboundFlights().size());
    assertFlight(
      response.getOutboundFlights().get(0),
      "MCT",
      "MCT",
      "IAD",
      "WAS"
    );
    
    assertNotNull(response.getReturnFlights());
    assertEquals(1, response.getReturnFlights().size());
    
    assertFlight(
      response.getReturnFlights().get(0),
      "IAD",
      "WAS",
      "MCT",
      "MCT"
    );
    //@formatter:on
  }
  
  @Test
  public void testRevalidateFlightsNoAuthorizationToken() {
    RevalidateFlightRequestDTO request = getRevalidateFlightsRequestBuilder().build();
    //@formatter:off
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/flights/revalidate")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  
  @Test
  public void testRevalidateFlights() throws JsonProcessingException {
    RevalidateFlightRequestDTO request = getRevalidateFlightsRequestBuilder().build();
    
    FlightResponseDTO response = FlightResponseDTO.builder()
      .additionalProperties(Map.of("key", "value"))
      .build();
    
    //@formatter:off
    stubFor(post(urlEqualTo("/sabre/revalidate"))
        .withRequestBody(matchingJsonPath("$.departureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.originLocationCode", equalTo("MCT")))
        .withRequestBody(matchingJsonPath("$.destinationLocationCode", equalTo("WAS")))
        .withRequestBody(matchingJsonPath("$[?(@.passengers.size() == 1)]"))
        .withRequestBody(matchingJsonPath("$.passengers[0].code", equalTo("ADT")))
        .withRequestBody(matchingJsonPath("$.passengers[0].quantity", equalTo("1")))
        
        .withRequestBody(matchingJsonPath("$[?(@.flightSegments.size() == 2)]"))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].number", equalTo("991")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].departureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].arrivalDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].type", equalTo("A")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].classOfService", equalTo("Y")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].originLocation", equalTo("MCT")))
        .withRequestBody(matchingJsonPath("$.flightSegments[0].destinationLocation", equalTo("DOH")))
        
        
        .withRequestBody(matchingJsonPath("$.flightSegments[1].number", equalTo("992")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].departureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].arrivalDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].type", equalTo("A")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].classOfService", equalTo("Y")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].originLocation", equalTo("DOH")))
        .withRequestBody(matchingJsonPath("$.flightSegments[1].destinationLocation", equalTo("IAD")))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", ContentType.JSON.toString())
        .withBody(this.objectMapper.writeValueAsString(response)))
    );
    
    
    given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/flights/revalidate")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("key", Matchers.equalTo("value"));
    //@formatter:on
  }
  
  private RevalidateFlightRequestDTO.RevalidateFlightRequestDTOBuilder getRevalidateFlightsRequestBuilder() {
    return RevalidateFlightRequestDTO.builder()
      .departureDateTime("2023-05-10T00:00:00")
      .originLocationCode("MCT")
      .destinationLocationCode("WAS")
      .passengers(
        List.of(Passenger.builder()
          .code(Passenger.TypeCodes.ADT)
          .quantity(1)
          .build()))
      .flightSegments(
        List.of(
          getFlightSegmentBuilder().originLocation("MCT").destinationLocation("DOH").number(991).build(),
          getFlightSegmentBuilder().originLocation("DOH").destinationLocation("IAD").number(992).build()
        )
      );
  }
  
  private RevalidateFlightRequestDTO.FlightSegment.FlightSegmentBuilder getFlightSegmentBuilder() {
    return RevalidateFlightRequestDTO.FlightSegment.builder()
      .number(991)
      .departureDateTime("2023-05-10T00:00:00")
      .arrivalDateTime("2023-05-10T00:00:00")
      .type("A")
      .classOfService("Y")
      .originLocation("MCT")
      .destinationLocation("DOH");
  }
  
  private SearchFlightRequestDTO.SearchFlightRequestDTOBuilder getSearchRequestBuilder() {
    return SearchFlightRequestDTO.builder()
      .cabin("Y")
      .departureDateTime("2023-05-10T00:00:00")
      .originLocationCode("MCT")
      .destinationLocationCode("WAS")
      .passengers(
        List.of(Passenger.builder()
          .code(Passenger.TypeCodes.ADT)
          .quantity(1)
          .build())
      );
  }
  
  private void stubForSearchFlights(SearchFlightResponseDTO mockedResponse,
                                    String fromLocation,
                                    String toLocation,
                                    String departureDate,
                                    String returnDate,
                                    boolean roundTrip) throws JsonProcessingException {
    MappingBuilder mappingBuilder = post(urlEqualTo("/sabre/search"));
    if (roundTrip) {
      mappingBuilder.withRequestBody(matchingJsonPath("$.returnDateTime", equalTo(returnDate)));
    }
    
    stubFor(mappingBuilder
      .withRequestBody(matchingJsonPath("$.roundTrip", equalTo(String.valueOf(roundTrip))))
      .withRequestBody(matchingJsonPath("$.departureDateTime", equalTo(departureDate)))
      .withRequestBody(matchingJsonPath("$.originLocationCode", equalTo(fromLocation)))
      .withRequestBody(matchingJsonPath("$.destinationLocationCode", equalTo(toLocation)))
      .withRequestBody(matchingJsonPath("$[?(@.passengers.size() == 1)]"))
      .withRequestBody(matchingJsonPath("$.passengers[0].code", equalTo("ADT")))
      .withRequestBody(matchingJsonPath("$.passengers[0].quantity", equalTo("1")))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", ContentType.JSON.toString())
        .withBody(this.objectMapper.writeValueAsString(mockedResponse))
      ));
  }
  
  private void assertFlight(Flight flight,
                            String fromAirport,
                            String fromCity,
                            String toAirport,
                            String toCity) {
    assertEquals(975, flight.getDuration());
    assertTrue(flight.getDirectFlight());
    assertEquals(1, flight.getSegments().size());
    
    FlightSegment outboundFlightSegment = flight.getSegments().get(0);
    assertTrue(outboundFlightSegment.getETicketable());
    assertEquals(1, outboundFlightSegment.getId());
    assertEquals(975, outboundFlightSegment.getElapsedTime());
    assertEquals(0, outboundFlightSegment.getStopCount());
    assertEquals("SMTWTFS", outboundFlightSegment.getFrequency());
    assertEquals(7000, outboundFlightSegment.getTotalMilesFlown());
    assertEquals(fromAirport, outboundFlightSegment.getDeparture().getAirport());
    assertEquals(fromCity, outboundFlightSegment.getDeparture().getCity());
    assertEquals(toAirport, outboundFlightSegment.getArrival().getAirport());
    assertEquals(toCity, outboundFlightSegment.getArrival().getCity());
  }
  
  private Flight getMockedFlight(String fromAirport, String fromCity, String toAirport, String toCity) {
    return Flight.builder()
      .directFlight(true)
      .duration(975)
      .segment(
        FlightSegment.builder()
          .id(1)
          .frequency("SMTWTFS")
          .elapsedTime(975)
          .stopCount(0)
          .eTicketable(true)
          .totalMilesFlown(7000)
          .departure(Departure.builder().airport(fromAirport).city(fromCity).build())
          .arrival(Arrival.builder().airport(toAirport).city(toCity).build())
          .build()
      ).build();
  }
  
}