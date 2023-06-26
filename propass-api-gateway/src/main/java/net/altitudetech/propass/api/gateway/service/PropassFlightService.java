package net.altitudetech.propass.api.gateway.service;

import net.altitudetech.propass.api.gateway.dto.SearchFlightResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.api.gateway.client.PropassFlightClient;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.FlightResponseDTO;
import net.altitudetech.propass.api.gateway.dto.RevalidateFlightRequestDTO;
import net.altitudetech.propass.api.gateway.dto.SearchFlightRequestDTO;
import net.altitudetech.propass.commons.client.RetrofitCaller;

@Service
public class PropassFlightService {
  
  @Autowired
  private RetrofitCaller retrofitCaller;
  
  @Autowired
  private PropassFlightClient propassFlightClient;
  
  public SearchFlightResponseDTO searchFlights(SearchFlightRequestDTO request) {
    return this.retrofitCaller.sync(this.propassFlightClient.searchFlights(request));
  }
  
  public FlightResponseDTO revalidateFlights(RevalidateFlightRequestDTO request) {
    return this.retrofitCaller.sync(this.propassFlightClient.revalidateFlights(request));
  }
  
  public FlightDTO findOne(Long id) {
    return this.retrofitCaller.sync(this.propassFlightClient.getFlight(id));
  }

  public Page<FlightDTO> findAll(FlightDTO filter, Pageable pageable) {
    return this.retrofitCaller.sync(filter, qm -> this.propassFlightClient.getFlights(qm, pageable));
  }
}
