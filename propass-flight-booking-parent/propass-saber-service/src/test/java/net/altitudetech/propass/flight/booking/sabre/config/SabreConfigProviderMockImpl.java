package net.altitudetech.propass.flight.booking.sabre.config;

import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Airline;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.CompanyName;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.DataSources;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.NumTrips;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Pos;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.PreferenceLevel;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.RequestorID;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.Source;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.TravelPreferences;
import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.VendorPref;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SabreConfigProviderMockImpl {
  
  @Bean
  @Primary
  public SabreConfigProvider sabreConfigProvider() {
    return (company) -> getMockConfig();
  }
  
  private SabreConfig getMockConfig() {
    Map<Integer, SabreConfig> configMap = new HashMap<>();
    CompanyName companyName = CompanyName.builder()
      .code("TN")
      .build();
    
    RequestorID requestorID = RequestorID.builder()
      .id("1")
      .type("1")
      .companyName(companyName)
      .build();
    
    Source source = Source.builder()
      .requestorID(requestorID)
      .pseudoCityCode("PCC1")
      .build();
    
    Pos pos = Pos.builder()
      .source(List.of(source))
      .build();
    
    DataSources dataSources = DataSources.builder()
      .atpco(DataSources.Status.ENABLE)
      .lcc(DataSources.Status.DISABLE)
      .ndc(DataSources.Status.ENABLE)
      .build();
    
    NumTrips numTrips = NumTrips.builder()
      .number(100)
      .build();
    
    TravelPreferences.TPAExtensions tpaExtensions = TravelPreferences.TPAExtensions.builder()
      .dataSources(dataSources)
      .numTrips(numTrips)
      .build();
    
    VendorPref vendorPref = VendorPref.builder()
      .preferLevel(PreferenceLevel.ONLY)
      .type(VendorPref.Type.MARKETING)
      .code("WY")
      .build();
    
    TravelPreferences travelPreferences = TravelPreferences.builder()
      .vendorPrefs(List.of(vendorPref))
      .tPAExtensions(tpaExtensions)
      .build();
    
    
    Airline airline = Airline.builder().marketing("WY").build();
    
    return SabreConfig.builder()
      .pos(pos)
      .travelPreferences(travelPreferences)
      .airline(airline)
      .directFlightsOnly(true)
      .dateWindowDays(1)
      .version("v4")
      .build();
  }
}
