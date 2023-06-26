package net.altitudetech.propass.flight.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.commons.service.BaseService;
import net.altitudetech.propass.flight.service.model.Flight;
import net.altitudetech.propass.flight.service.repository.FlightRepository;
import net.altitudetech.propass.flight.service.specification.FlightSpecification;

@Service
public class FlightService extends BaseService<Flight, FlightRepository> {
  public FlightService(FlightRepository repository) {
    super(repository);
  }
  
  public Page<Flight> findAll(FlightSpecification specification, Pageable pageable) {
    return this.repository.findAll(specification, pageable);
  }
}
