package net.altitudetech.propass.flight.booking.sabre.config;

import net.altitudetech.propass.flight.booking.sabre.dto.integration.request.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO:
//  1. This just for the PoC, since I am not sure if all of these config will be per company (airline) or some of them might be per user
//  2. Decide where and how to handle configuration for different companies/airlines
@Configuration
public class InMemorySabreConfigProvider implements SabreConfigProvider {
  // TODO:
  //  * WY: the marketing code of Omanair
  //  * We put the below as app configurations just for simplicity, in case we need to change it and do PoC for other airlines
  //  * In Prod they will be a config per Company
  @Value("${propass.service.booking.flight.sabre.airline.marketing-code:WY}")
  private String airlineMarketingCode;
  @Value("${propass.service.booking.flight.sabre.direct-flights-only:true}")
  private boolean directFlightsOnly;
  @Value("${propass.service.booking.flight.sabre.date-window-days:1}")
  private int dateWindowDays;
  
  private static Map<Integer, SabreConfig> configMap = new HashMap<>();
  
  static {
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
            .pseudoCityCode("F9CE")
            .build();
    
    Pos pos = Pos.builder()
            .source(List.of(source))
            .build();
    
    DataSources dataSources = DataSources.builder()
            .atpco(DataSources.Status.ENABLE)
            .lcc(DataSources.Status.DISABLE)
            .ndc(DataSources.Status.DISABLE)
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
            .build();
    
    TravelPreferences travelPreferences = TravelPreferences.builder()
            .vendorPrefs(List.of(vendorPref))
            .tPAExtensions(tpaExtensions)
            .build();
    
    
    Airline airline = Airline.builder().build();
    
    SabreConfig config = SabreConfig.builder()
            .pos(pos)
            .travelPreferences(travelPreferences)
            .airline(airline)
            .version("v4")
            .build();
    
    configMap.put(1, config);
  }
  
  @Override
  public SabreConfig getConfig(int company) {
    SabreConfig sabreConfig = configMap.get(company);
    sabreConfig.getTravelPreferences().getVendorPrefs().get(0).setCode(airlineMarketingCode);
    sabreConfig.getAirline().setMarketing(airlineMarketingCode);
    sabreConfig.setDirectFlightsOnly(directFlightsOnly);
    sabreConfig.setDateWindowDays(dateWindowDays);
    return sabreConfig;
  }
}
