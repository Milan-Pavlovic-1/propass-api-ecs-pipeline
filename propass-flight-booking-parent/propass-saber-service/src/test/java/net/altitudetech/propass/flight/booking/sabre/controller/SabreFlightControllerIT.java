package net.altitudetech.propass.flight.booking.sabre.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.flight.booking.sabre.config.PropassSabreTestConfig;
import net.altitudetech.propass.flight.booking.sabre.config.SabreConfigProviderMockImpl;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.Arrival;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.Departure;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.GroupedItineraryResponse;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.LegDescs;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.ScheduleDesc;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.Statistics;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightResponseDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.Flight;
import net.altitudetech.propass.flight.booking.sabre.dto.search.Passenger;
import net.altitudetech.propass.flight.booking.sabre.dto.search.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightResponseDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassSabreTestConfig.class, SabreConfigProviderMockImpl.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SabreFlightControllerIT {
  
  @LocalServerPort
  private int port;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
    WireMock.reset();
  }
  
  @Test
  public void testSearchFlightsNoAuthorizationToken() {
    SearchFlightRequestDTO request = getSearchRequestBuilder().build();
    //@formatter:off
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/sabre/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  
  @Test
  public void testSearchFlightsOneWay() throws JsonProcessingException {
    LocalDateTime departureDate = LocalDateTime.parse("2023-05-10T00:00:00");
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .departureDateTime(departureDate)
      .build();
    
    //@formatter:off
    stubForSearchFlights("MCT", "WAS", departureDate,null, false);

    SearchFlightResponseDTO response = given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/sabre/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
      .extract().body().as(SearchFlightResponseDTO.class);
    
    assertTrue(response.getReturnFlights().isEmpty());
    assertNotNull(response.getOutboundFlights());
    assertEquals(1, response.getOutboundFlights().size());
    Flight flight = response.getOutboundFlights().get(0);
    assertEquals(975, flight.getDuration());
    assertTrue(flight.getDirectFlight());
    assertEquals(1,flight.getSegments().size());
    assertEquals(departureDate.format(ISO_LOCAL_DATE), flight.getDepartureDate());
    assertEquals("MCT", flight.getSegments().get(0).getDeparture().getAirport());
    assertEquals("MCT", flight.getSegments().get(0).getDeparture().getCity());
    assertEquals("IAD", flight.getSegments().get(0).getArrival().getAirport());
    assertEquals("WAS", flight.getSegments().get(0).getArrival().getCity());
    //@formatter:on
  }
  
  @Test
  public void testSearchFlightsRoundTrip() throws JsonProcessingException {
    LocalDateTime departureDate = LocalDateTime.parse("2023-05-10T00:00:00");
    LocalDateTime returnDate = LocalDateTime.parse("2023-06-10T00:00:00");
    stubForSearchFlights("MCT", "WAS", departureDate, returnDate, true);
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .departureDateTime(departureDate)
      .returnDateTime(returnDate)
      .roundTrip(true)
      .build();
    
    //@formatter:off
    
    SearchFlightResponseDTO response = given()
      .when()
      .contentType(ContentType.JSON)
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .body(request)
      .post("/sabre/search")
      .then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.OK.value())
      .extract().body().as(SearchFlightResponseDTO.class);
    
    assertNotNull(response.getReturnFlights());
    assertNotNull(response.getOutboundFlights());
    assertEquals(1, response.getReturnFlights().size());
    assertEquals(1, response.getOutboundFlights().size());
    
    Flight outboundFlight = response.getOutboundFlights().get(0);
    assertEquals(975, outboundFlight.getDuration());
    assertTrue(outboundFlight.getDirectFlight());
    assertEquals(1, outboundFlight.getSegments().size());
    assertEquals(departureDate.format(ISO_LOCAL_DATE), outboundFlight.getDepartureDate());
    assertEquals("MCT", outboundFlight.getSegments().get(0).getDeparture().getAirport());
    assertEquals("MCT", outboundFlight.getSegments().get(0).getDeparture().getCity());
    assertEquals("IAD", outboundFlight.getSegments().get(0).getArrival().getAirport());
    assertEquals("WAS", outboundFlight.getSegments().get(0).getArrival().getCity());
    
    Flight returnFlight = response.getReturnFlights().get(0);
    assertEquals(975, returnFlight.getDuration());
    assertTrue(returnFlight.getDirectFlight());
    assertEquals(1, returnFlight.getSegments().size());
    assertEquals(returnDate.format(ISO_LOCAL_DATE), returnFlight.getDepartureDate());
    assertEquals("IAD", returnFlight.getSegments().get(0).getDeparture().getAirport());
    assertEquals("WAS", returnFlight.getSegments().get(0).getDeparture().getCity());
    assertEquals("MCT", returnFlight.getSegments().get(0).getArrival().getAirport());
    assertEquals("MCT", returnFlight.getSegments().get(0).getArrival().getCity());
    //@formatter:on
  }
  
  @Test
  public void testSearchFlightsDateWindow() throws JsonProcessingException {
    final LocalDateTime departureDate = LocalDateTime.parse("2023-05-10T00:00:00");
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .departureDateTime(departureDate)
      .roundTrip(false)
      .build();
    
    SabreSearchFlightResponseDTO mockedResponse = getMockedResponse(false);
    mockedResponse.getGroupedItineraryResponse().getStatistics().setItineraryCount(0);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate, null, false);
    
    mockedResponse.getGroupedItineraryResponse().getStatistics().setItineraryCount(2);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate.plusDays(1), null, false);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate.minusDays(1), null, false);
    
    //@formatter:off
    SearchFlightResponseDTO response = given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/sabre/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().as(SearchFlightResponseDTO.class);
    
    assertTrue(response.getReturnFlights().isEmpty());
    assertNotNull(response.getOutboundFlights());
    assertEquals(2, response.getOutboundFlights().size());
    boolean found = response.getOutboundFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(departureDate.plusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);
    
    found = response.getOutboundFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(departureDate.minusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);
    //@formatter:on
    
  }
  
  @Test
  public void testSearchFlightsDateWindowRoundTrip() throws JsonProcessingException {
    final LocalDateTime departureDate = LocalDateTime.parse("2023-05-10T00:00:00");
    final LocalDateTime returnDate = LocalDateTime.parse("2023-06-10T00:00:00");
    SearchFlightRequestDTO request = getSearchRequestBuilder()
      .departureDateTime(departureDate)
      .returnDateTime(returnDate)
      .roundTrip(true)
      .build();
    
    SabreSearchFlightResponseDTO mockedResponse = getMockedResponse(true);
    mockedResponse.getGroupedItineraryResponse().getStatistics().setItineraryCount(0);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate, returnDate, true);
    
    mockedResponse.getGroupedItineraryResponse().getStatistics().setItineraryCount(2);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate.plusDays(1), returnDate.plusDays(1), true);
    stubForSearchFlights(mockedResponse, "MCT", "WAS", departureDate.minusDays(1), returnDate.minusDays(1), true);
    
    //@formatter:off
    SearchFlightResponseDTO response = given()
      .when()
        .contentType(ContentType.JSON)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .body(request)
        .post("/sabre/search")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().as(SearchFlightResponseDTO.class);
    
    
    assertNotNull(response.getOutboundFlights());
//    assertEquals(2, response.getOutboundFlights().size());
    boolean found = response.getOutboundFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(departureDate.plusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);
    
    found = response.getOutboundFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(departureDate.minusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);
    
    assertNotNull(response.getReturnFlights());
    assertEquals(2, response.getReturnFlights().size());
    found = response.getReturnFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(returnDate.plusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);

    found = response.getReturnFlights()
      .stream()
      .anyMatch(flight -> flight.getDepartureDate().equals(returnDate.minusDays(1).format(ISO_LOCAL_DATE)));
    assertTrue(found);
    //@formatter:on
    
  }
  
  @Test
  public void testRevalidateFlightsNoAuthorizationToken() {
    SearchFlightRequestDTO request = getSearchRequestBuilder().build();
    //@formatter:off
    given()
      .when()
      .contentType(ContentType.JSON)
      .body(request)
      .post("/sabre/revalidate")
      .then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  @Test
  public void testRevalidateFlights() throws JsonProcessingException {
    RevalidateFlightRequestDTO request = getRevalidateFlightsRequestBuilder().build();
    SabreSearchFlightResponseDTO response = SabreSearchFlightResponseDTO.builder()
      .groupedItineraryResponse(
        GroupedItineraryResponse.builder().additionalProperties(Map.of("key", "value")).build()
      ).build();
    
    //@formatter:off
    stubFor(sharedStubMapping("/v4/shop/flights/revalidate")
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].DepartureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].OriginLocation.LocationCode", equalTo("MCT")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].DestinationLocation.LocationCode", equalTo("WAS")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".TravelerInfoSummary.AirTravelerAvail[0].PassengerTypeQuantity[0].Code", equalTo("ADT")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".TravelerInfoSummary.AirTravelerAvail[0].PassengerTypeQuantity[0].Quantity", equalTo("1")))
        
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.[?(@.Flight.size() == 2)]"))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].Number", equalTo("991")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].DepartureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].ArrivalDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].Type", equalTo("A")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].ClassOfService", equalTo("Y")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].OriginLocation.LocationCode", equalTo("MCT")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].DestinationLocation.LocationCode", equalTo("DOH")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[0].Airline.Marketing", equalTo("WY")))
        
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].Number", equalTo("992")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].DepartureDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].ArrivalDateTime", equalTo("2023-05-10T00:00:00")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].Type", equalTo("A")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].ClassOfService", equalTo("Y")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].OriginLocation.LocationCode", equalTo("DOH")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].DestinationLocation.LocationCode", equalTo("IAD")))
        .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
          ".OriginDestinationInformation[0].TPA_Extensions.Flight[1].Airline.Marketing", equalTo("WY")))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", ContentType.JSON.toString())
        .withBody(this.objectMapper.writeValueAsString(response))
      )
    );
    
    given()
      .when()
      .contentType(ContentType.JSON)
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .body(request)
      .post("/sabre/revalidate")
      .then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.OK.value())
      .body("groupedItineraryResponse.key", Matchers.equalTo("value"));
    //@formatter:on
  }
  
  private MappingBuilder sharedStubMapping(String url) {
    return post(urlEqualTo(url))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.Version", equalTo("v4")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.POS.Source[0].PseudoCityCode", equalTo("PCC1")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.POS.Source[0].RequestorID.Type", equalTo("1")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.POS.Source[0].RequestorID.ID", equalTo("1")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.POS.Source[0].RequestorID.CompanyName.Code", equalTo("TN")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.VendorPref[0].Type", equalTo("Marketing")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.VendorPref[0].PreferLevel", equalTo("Only")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.VendorPref[0].Code", equalTo("WY")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.TPA_Extensions.NumTrips.Number", equalTo("100")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.TPA_Extensions.DataSources.NDC", equalTo("Enable")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.TPA_Extensions.DataSources.ATPCO", equalTo("Enable")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ.TravelPreferences.TPA_Extensions.DataSources.LCC", equalTo("Disable")));
  }
  
  private void stubForSearchFlights(String fromLocation,
                                    String toLocation,
                                    LocalDateTime departureDate,
                                    LocalDateTime returnDate,
                                    boolean roundTrip) throws JsonProcessingException {
    SabreSearchFlightResponseDTO sabreResponse = getMockedResponse(roundTrip);
    stubForSearchFlights(sabreResponse, fromLocation, toLocation, departureDate, returnDate, roundTrip);
  }
  
  private void stubForSearchFlights(SabreSearchFlightResponseDTO sabreResponse,
                                    String fromLocation,
                                    String toLocation,
                                    LocalDateTime departureDate,
                                    LocalDateTime returnDate,
                                    boolean roundTrip) throws JsonProcessingException {
    MappingBuilder mappingBuilder = sharedStubMapping("/v4/offers/shop");
    
    addOriginsDestinationsMappings(mappingBuilder, fromLocation, toLocation, departureDate, 0);
    if (roundTrip) {
      addOriginsDestinationsMappings(mappingBuilder, toLocation, fromLocation, returnDate, 1);
    }
    stubFor(mappingBuilder
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
        ".TravelerInfoSummary.AirTravelerAvail[0].PassengerTypeQuantity[0].Code", equalTo("ADT")))
      .withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
        ".TravelerInfoSummary.AirTravelerAvail[0].PassengerTypeQuantity[0].Quantity", equalTo("1")))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", ContentType.JSON.toString())
        .withBody(this.objectMapper.writeValueAsString(sabreResponse))
      )
    );
  }
  
  private void addOriginsDestinationsMappings(MappingBuilder mappingBuilder,
                                              String fromLocation,
                                              String toLocation,
                                              LocalDateTime departureDate,
                                              int itemIndex) {
    
    mappingBuilder.withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
      ".OriginDestinationInformation[" + itemIndex + "].DepartureDateTime", equalTo(departureDate.format(ISO_LOCAL_DATE_TIME))));
    mappingBuilder.withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
      ".OriginDestinationInformation[" + itemIndex + "].OriginLocation.LocationCode", equalTo(fromLocation)));
    mappingBuilder.withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
      ".OriginDestinationInformation[" + itemIndex + "].DestinationLocation.LocationCode", equalTo(toLocation)));
    mappingBuilder.withRequestBody(matchingJsonPath("$.OTA_AirLowFareSearchRQ" +
      ".OriginDestinationInformation[" + itemIndex + "].TPA_Extensions.CabinPref.Cabin", equalTo("Y")));
  }
  
  private SabreSearchFlightResponseDTO getMockedResponse(boolean roundTrip) {
    GroupedItineraryResponse.GroupedItineraryResponseBuilder groupedItineraryResponseBuilder = GroupedItineraryResponse.builder()
      .statistics(Statistics.of(2))
      
      // MCT => WAS
      .scheduleDesc(getMockedSchedule(1, 90, "MCT", "MCT", "DOH", "DOH"))
      .scheduleDesc(getMockedSchedule(2, 885, "DOH", "DOH", "IAD", "WAS"))
      .scheduleDesc(getMockedSchedule(3, 975, "MCT", "MCT", "IAD", "WAS"))
      .legDesc(getMockedLegs(1175, 1, 2))
      .legDesc(getMockedLegs(975, 3));
    
    if (roundTrip) {
      // WAS => MCT
      groupedItineraryResponseBuilder
        .scheduleDesc(getMockedSchedule(4, 885, "IAD", "WAS", "DOH", "DOH"))
        .scheduleDesc(getMockedSchedule(5, 90, "DOH", "DOH", "MCT", "MCT"))
        .scheduleDesc(getMockedSchedule(6, 975, "IAD", "WAS", "MCT", "MCT"))
        .legDesc(getMockedLegs(1175, 4, 5))
        .legDesc(getMockedLegs(975, 6));
    }
    return SabreSearchFlightResponseDTO.builder()
      .groupedItineraryResponse(groupedItineraryResponseBuilder.build())
      .build();
  }
  
  private LegDescs getMockedLegs(int elapsedTime, int... segmentsRefs) {
    LegDescs.LegDescsBuilder legDescsBuilder = LegDescs.builder().elapsedTime(elapsedTime);
    for (int segmentsRef : segmentsRefs) {
      legDescsBuilder.schedule(LegDescs.Schedule.builder().ref(segmentsRef).build());
    }
    return legDescsBuilder.build();
  }
  
  private ScheduleDesc getMockedSchedule(int id, int duration, String fromAirport, String fromCity, String toAirport, String toCity) {
    return ScheduleDesc.builder()
      .id(id)
      .elapsedTime(duration)
      .departure(Departure.builder().airport(fromAirport).city(fromCity).build())
      .arrival(Arrival.builder().airport(toAirport).city(toCity).build())
      .build();
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
      .departureDateTime(LocalDateTime.parse("2023-05-10T00:00:00"))
      .originLocationCode("MCT")
      .destinationLocationCode("WAS")
      .passengers(
        List.of(Passenger.builder()
          .code(Passenger.TypeCodes.ADT)
          .quantity(1)
          .build())
      );
  }
}