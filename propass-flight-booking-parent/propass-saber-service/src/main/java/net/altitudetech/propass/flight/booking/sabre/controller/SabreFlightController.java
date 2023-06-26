package net.altitudetech.propass.flight.booking.sabre.controller;

import net.altitudetech.propass.flight.booking.sabre.config.SabreConfig;
import net.altitudetech.propass.flight.booking.sabre.config.SabreConfigProvider;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SabreContext;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.AirTravelerAvail;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Airline;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.DestinationLocation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Flight;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OTAAirLowFareSearchRQ;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginDestinationInformation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginLocation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.PassengerTypeQuantity;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.TravelerInfoSummary;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightResponseDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.Passenger;
import net.altitudetech.propass.flight.booking.sabre.dto.search.RevalidateFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightResponseDTO;
import net.altitudetech.propass.flight.booking.sabre.service.SabreFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginDestinationInformation.TPAExtensions;
import static net.altitudetech.propass.flight.booking.sabre.dto.search.RevalidateFlightRequestDTO.FlightSegment;


@RestController
@RequestMapping("/sabre")
public class SabreFlightController {
  
  
  @Autowired
  private SabreConfigProvider sabreConfigProvider;
  @Autowired
  private SabreFlightService sabreFlightService;
  
  @PostMapping("/search")
  public SearchFlightResponseDTO searchFlights(@RequestBody SearchFlightRequestDTO request) {
//    TODO: a better way to map models
    List<PassengerTypeQuantity> passengers = mapList(request.getPassengers(), this::mapPassengerToPassengerTypeQuantity);
    
    AirTravelerAvail airTravelerAvail = AirTravelerAvail.of(passengers);
    TravelerInfoSummary travelerInfoSummary = TravelerInfoSummary.of(List.of(airTravelerAvail));
    
    OTAAirLowFareSearchRQ oTAAirLowFareSearchRQ = OTAAirLowFareSearchRQ.builder()
      .originDestinationInformation(new ArrayList<>())
      .travelerInfoSummary(travelerInfoSummary)
      .build();
    
    SabreSearchFlightRequestDTO sabreRequest = SabreSearchFlightRequestDTO.of(oTAAirLowFareSearchRQ);
    addCompanyData(sabreRequest);
    
    SabreContext<SabreSearchFlightRequestDTO, SabreSearchFlightResponseDTO> sabreContext = new SabreContext<>(sabreConfigProvider.getConfig(1));
    sabreContext.setRequest(sabreRequest);
    
    return this.sabreFlightService.searchFlights(sabreContext, request);
  }
  
  @PostMapping("/revalidate")
  public SabreSearchFlightResponseDTO revalidateFlights(@RequestBody RevalidateFlightRequestDTO request) {
//    TODO: decide how to handle different companies/airlines
    SabreConfig sabreConfig = sabreConfigProvider.getConfig(1);

//    TODO: a better way to map models
    List<Flight> flights = mapList(request.getFlightSegments(),
      flightSegment -> mapFlightSegmentToFlight(flightSegment, sabreConfig.getAirline()));
    
    
    OriginDestinationInformation originDestinationInformation = OriginDestinationInformation.builder()
      .departureDateTime(request.getDepartureDateTime())
      .originLocation(OriginLocation.of(request.getOriginLocationCode()))
      .destinationLocation(DestinationLocation.of(request.getDestinationLocationCode()))
      .tPAExtensions(TPAExtensions.of(flights))
      .build();
    
    List<PassengerTypeQuantity> passengers = mapList(request.getPassengers(), this::mapPassengerToPassengerTypeQuantity);
    
    AirTravelerAvail airTravelerAvail = AirTravelerAvail.of(passengers);
    TravelerInfoSummary travelerInfoSummary = TravelerInfoSummary.of(List.of(airTravelerAvail));
    
    OTAAirLowFareSearchRQ oTAAirLowFareSearchRQ = OTAAirLowFareSearchRQ.builder()
      .originDestinationInformation(List.of(originDestinationInformation))
      .travelerInfoSummary(travelerInfoSummary)
      .build();
    
    SabreSearchFlightRequestDTO newRequest = SabreSearchFlightRequestDTO.of(oTAAirLowFareSearchRQ);
    addCompanyData(newRequest);
    
    return this.sabreFlightService.revalidateFlights(newRequest);
  }
  
  
  private <T, V> List<V> mapList(List<T> inputs, Function<T, V> mapper) {
    List<V> output = new ArrayList<>();
    for (T input : inputs) {
      output.add(mapper.apply(input));
    }
    return output;
  }
  
  private PassengerTypeQuantity mapPassengerToPassengerTypeQuantity(Passenger passenger) {
    return PassengerTypeQuantity.of(passenger.getCode().toString(), passenger.getQuantity());
    
  }
  
  private Flight mapFlightSegmentToFlight(FlightSegment segment, Airline airline) {
    return Flight.builder()
      .departureDateTime(segment.getDepartureDateTime())
      .arrivalDateTime(segment.getArrivalDateTime())
      .number(segment.getNumber())
      .type(segment.getType())
      .classOfService(segment.getClassOfService())
      .airline(airline)
      .originLocation(OriginLocation.of(segment.getOriginLocation()))
      .destinationLocation(DestinationLocation.of(segment.getDestinationLocation()))
      .build();
  }
  
  private void addCompanyData(SabreSearchFlightRequestDTO request) {
//    TODO: decide how to handle different companies/airlines
    SabreConfig sabreConfig = sabreConfigProvider.getConfig(1);
    
    request.getOTAAirLowFareSearchRQ().setVersion(sabreConfig.getVersion());
    request.getOTAAirLowFareSearchRQ().setPos(sabreConfig.getPos());
    request.getOTAAirLowFareSearchRQ().setTravelPreferences(sabreConfig.getTravelPreferences());
    
  }
}
