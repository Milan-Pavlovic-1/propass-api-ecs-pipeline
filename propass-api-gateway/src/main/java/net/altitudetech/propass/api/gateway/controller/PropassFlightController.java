package net.altitudetech.propass.api.gateway.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.altitudetech.propass.api.gateway.dto.FlightResponseDTO;
import net.altitudetech.propass.api.gateway.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.api.gateway.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.api.gateway.dto.SearchFlightResponseDTO;
import net.altitudetech.propass.api.gateway.service.PropassFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.commons.security.annotation.Public;

@RestController
@RequestMapping("/propass/flights")
public class PropassFlightController {
  
  @Autowired
  private PropassFlightService propassFlightService;
  
  @PostMapping("/search")
  @SecurityRequirement(name = "BearerAuth")
  public SearchFlightResponseDTO searchFlights(@RequestBody SearchFlightRequestDTO request) {
    return this.propassFlightService.searchFlights(request);
  }
  
  @PostMapping("/revalidate")
  @SecurityRequirement(name = "BearerAuth")
  public FlightResponseDTO revalidateFlights(@RequestBody RevalidateFlightRequestDTO request) {
    return this.propassFlightService.revalidateFlights(request);
  }
  
  @Public
  @GetMapping("/{flightId}")
  public FlightDTO single(@PathVariable Long flightId) {
    return this.propassFlightService.findOne(flightId);
  }
  
  @Public
  @GetMapping
  public Page<FlightDTO> all(FlightDTO filter, Pageable pageable) {
    return this.propassFlightService.findAll(filter, pageable);
  }
  
}
