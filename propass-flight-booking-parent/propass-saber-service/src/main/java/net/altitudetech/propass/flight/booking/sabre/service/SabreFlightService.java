package net.altitudetech.propass.flight.booking.sabre.service;

import net.altitudetech.propass.commons.client.RetrofitCaller;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.flight.booking.sabre.client.SabreFlightClient;
import net.altitudetech.propass.flight.booking.sabre.config.SabreConfigProvider;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.CabinPref;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.DestinationLocation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginDestinationInformation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginLocation;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.LegDescs;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.response.ScheduleDesc;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.search.SabreSearchFlightResponseDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.Flight;
import net.altitudetech.propass.flight.booking.sabre.dto.search.PropassContext;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SabreContext;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightRequestDTO;
import net.altitudetech.propass.flight.booking.sabre.dto.search.SearchFlightResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static net.altitudetech.propass.flight.booking.sabre.dto.integration.request.OriginDestinationInformation.TPAExtensions;


@Service
public class SabreFlightService {
  
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private SabreFlightClient sabreFlightClient;
  @Autowired
  private SabreConfigProvider sabreConfigProvider;
  
  public SearchFlightResponseDTO searchFlights(SabreContext<SabreSearchFlightRequestDTO, SabreSearchFlightResponseDTO> sabreContext,
                                               SearchFlightRequestDTO propassRequest) {
//    TODO: decide how to handle different companies/airlines
    
    PropassContext<SearchFlightRequestDTO, SearchFlightResponseDTO.SearchFlightResponseDTOBuilder> propassContext =
      new PropassContext<>(propassRequest, SearchFlightResponseDTO.builder());
    
    callSabreSearchAPI(sabreContext, propassRequest);
    boolean foundFlights = processSabreResponse(sabreContext, propassContext);
    
    if (!foundFlights) {
      searchFlightsUsingDateWindow(sabreContext, propassContext);
    }
    
    SearchFlightResponseDTO response = propassContext.getResponse().build();
    if (response.getOutboundFlights().size() == 0) {
      throw new NotFoundException("No available flight schedules for qualifiers used");
      
    }
    return response;
  }
  
  public SabreSearchFlightResponseDTO revalidateFlights(SabreSearchFlightRequestDTO request) {
    // TODO: error handling
    return this.retrofitCaller.sync(this.sabreFlightClient.revalidateFlights(request));
  }
  
  private void searchFlightsUsingDateWindow(SabreContext<SabreSearchFlightRequestDTO, SabreSearchFlightResponseDTO> sabreContext,
                                            PropassContext<SearchFlightRequestDTO, SearchFlightResponseDTO.SearchFlightResponseDTOBuilder> propassContext) {
    SearchFlightRequestDTO propassRequest = propassContext.getRequest();
    LocalDateTime baseDepartureDate = propassRequest.getDepartureDateTime();
    LocalDateTime baseReturnDate = propassRequest.getReturnDateTime();
    for (int days = sabreContext.getConfig().getDateWindowDays(); days > 0; days--) {
      addDatesToRequest(propassRequest, baseDepartureDate, baseReturnDate, days);
      callSabreSearchAPI(sabreContext, propassRequest);
      processSabreResponse(sabreContext, propassContext);
      
      addDatesToRequest(propassRequest, baseDepartureDate, baseReturnDate, -days);
      callSabreSearchAPI(sabreContext, propassRequest);
      processSabreResponse(sabreContext, propassContext);
    }
  }
  
  private void callSabreSearchAPI(SabreContext<SabreSearchFlightRequestDTO, SabreSearchFlightResponseDTO> sabreContext,
                                  SearchFlightRequestDTO propassRequest) {
    addOriginDestination(sabreContext.getRequest(), propassRequest);
    SabreSearchFlightResponseDTO response = this.retrofitCaller.sync(this.sabreFlightClient.searchFlights(sabreContext.getRequest()));
    sabreContext.setResponse(response);
  }
  
  private void addDatesToRequest(SearchFlightRequestDTO propassRequest,
                                 LocalDateTime baseDepartureDate, LocalDateTime baseReturnDate, int days) {
    propassRequest.setDepartureDateTime(baseDepartureDate.plusDays(days));
    if (propassRequest.isRoundTrip()) {
      propassRequest.setReturnDateTime(baseReturnDate.plusDays(days));
    }
  }
  
  private boolean processSabreResponse(SabreContext<SabreSearchFlightRequestDTO, SabreSearchFlightResponseDTO> sabreContext,
                                       PropassContext<SearchFlightRequestDTO, SearchFlightResponseDTO.SearchFlightResponseDTOBuilder> propassContext) {
    SabreSearchFlightResponseDTO sabreResponse = sabreContext.getResponse();
    
    if (!hasFlights(sabreResponse)) {
      return false;
    }
    boolean foundFlights = false;
    for (LegDescs legDesc : sabreResponse.getGroupedItineraryResponse().getLegDescs()) {
      if (sabreContext.getConfig().isDirectFlightsOnly()) {
        if (hasOneSegment(legDesc)) {
          foundFlights = true;
          Flight flight = Flight.builder()
            .directFlight(true)
            .duration(legDesc.getElapsedTime())
            .segment(getFlightSegment(sabreResponse, legDesc.getSchedules().get(0).getRef(), 1))
            .build();
          addFlightToResponse(flight, propassContext);
        }
      } else {
        foundFlights = true;
        Flight.FlightBuilder flightBuilder = Flight.builder()
          .duration(legDesc.getElapsedTime())
          .directFlight(!hasMoreThanOneSegment(legDesc));
        for (int i = 0; i < legDesc.getSchedules().size(); i++) {
          LegDescs.Schedule schedule = legDesc.getSchedules().get(i);
          flightBuilder.segment(getFlightSegment(sabreResponse, schedule.getRef(), i + 1));
        }
        Flight flight = flightBuilder.build();
        addFlightToResponse(flight, propassContext);
      }
    }
    return foundFlights;
  }
  
  private boolean hasFlights(SabreSearchFlightResponseDTO response) {
    return response.getGroupedItineraryResponse().getStatistics().getItineraryCount() != 0;
  }
  
  private void addOriginDestination(SabreSearchFlightRequestDTO sabreRequest,
                                    SearchFlightRequestDTO propassRequest) {
    List<OriginDestinationInformation> originDestinationInformation = new ArrayList<>();
    
    OriginDestinationInformation outboundLegOriginDestination = getOriginDestinationInformation(
      dateTimeToString(propassRequest.getDepartureDateTime()),
      propassRequest.getOriginLocationCode(),
      propassRequest.getDestinationLocationCode(),
      propassRequest.getCabin()
    );
    originDestinationInformation.add(outboundLegOriginDestination);
    
    if (propassRequest.isRoundTrip()) {
      OriginDestinationInformation returnOriginDestination = getOriginDestinationInformation(
        dateTimeToString(propassRequest.getReturnDateTime()),
        propassRequest.getDestinationLocationCode(),
        propassRequest.getOriginLocationCode(),
        propassRequest.getCabin()
      );
      originDestinationInformation.add(returnOriginDestination);
    }
    sabreRequest.getOTAAirLowFareSearchRQ().setOriginDestinationInformation(originDestinationInformation);
  }
  
  private String dateTimeToString(LocalDateTime localDateTime, DateTimeFormatter formatter) {
    return localDateTime.format(formatter);
  }
  
  private String dateTimeToString(LocalDateTime localDateTime) {
    return dateTimeToString(localDateTime, ISO_LOCAL_DATE_TIME);
  }
  
  private OriginDestinationInformation getOriginDestinationInformation(String departureDateTime,
                                                                       String fromLocation,
                                                                       String toLocation,
                                                                       String cabin) {
    TPAExtensions tpaExtensions = TPAExtensions.of(CabinPref.of(cabin));
    return OriginDestinationInformation.builder()
      .departureDateTime(departureDateTime)
      .originLocation(OriginLocation.of(fromLocation))
      .destinationLocation(DestinationLocation.of(toLocation))
      .tPAExtensions(tpaExtensions)
      .build();
  }
  
  private void addFlightToResponse(Flight flight,
                                   PropassContext<SearchFlightRequestDTO, SearchFlightResponseDTO.SearchFlightResponseDTOBuilder> propassContext) {
    ScheduleDesc flightFirstSegment = flight.getSegments().get(0);
    SearchFlightRequestDTO request = propassContext.getRequest();
    if (isOutboundLeg(flightFirstSegment, request.getOriginLocationCode())) {
      flight.setDepartureDate(dateTimeToString(request.getDepartureDateTime(), ISO_LOCAL_DATE));
      propassContext.getResponse().outboundFlight(flight);
    } else {
      flight.setDepartureDate(dateTimeToString(request.getReturnDateTime(), ISO_LOCAL_DATE));
      propassContext.getResponse().returnFlight(flight);
    }
  }
  
  private boolean isOutboundLeg(ScheduleDesc flightFirstSegment, String originLocationCode) {
    return flightFirstSegment.getDeparture().getAirport().equals(originLocationCode)
      || flightFirstSegment.getDeparture().getCity().equals(originLocationCode);
  }
  
  private ScheduleDesc getFlightSegment(SabreSearchFlightResponseDTO sabreResponse, int segmentRef, int segmentId) {
    ScheduleDesc scheduleDesc = sabreResponse.getGroupedItineraryResponse()
      .getScheduleDescs()
      .get(segmentRef - 1);// one to zero based index
    scheduleDesc.setId(segmentId);
    return scheduleDesc;
  }
  
  private boolean hasMoreThanOneSegment(LegDescs legDesc) {
    return legDesc.getSchedules().size() > 1;
  }
  
  private boolean hasOneSegment(LegDescs legDesc) {
    return legDesc.getSchedules().size() == 1;
  }
}
