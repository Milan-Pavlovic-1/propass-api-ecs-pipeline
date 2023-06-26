package net.altitudetech.propass.flight.service.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.commons.controller.BaseController;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.commons.security.annotation.Public;
import net.altitudetech.propass.flight.service.dto.FlightDTO;
import net.altitudetech.propass.flight.service.model.Flight;
import net.altitudetech.propass.flight.service.service.FlightService;
import net.altitudetech.propass.flight.service.specification.FlightSpecification;

@RestController
@RequestMapping("/flights")
public class FlightController extends BaseController<Flight, FlightDTO> {
  @Autowired
  private FlightService flightService;

  @Autowired
  public FlightController(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @Public
  @GetMapping
  public Page<FlightDTO> list(Flight filter, Pageable pageable) {
    return toDTOs(this.flightService
        .findAll(new FlightSpecification(filter), pageable));
  }

  @Public
  @GetMapping("/{id}")
  public FlightDTO single(@PathVariable Long id) {
    Flight flight = this.flightService.findOne(id)
        .orElseThrow(() -> new NotFoundException("Flight " + id + " not found."));
    return toDTO(flight);
  }

  @PostMapping
  public FlightDTO create(@RequestBody FlightDTO flight) {
    return toDTO(this.flightService.save(toEntity(flight)));
  }

  @PutMapping("/{id}")
  public FlightDTO update(@PathVariable Long id, @RequestBody FlightDTO flight) {
    flight.setId(id);
    return toDTO(this.flightService.save(toEntity(flight)));
  }
}
