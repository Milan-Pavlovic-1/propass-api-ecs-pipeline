package net.altitudetech.propass.flight.booking.sabre.dto.integration.request;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PreferenceLevel {
  ONLY("Only"), PREFERRED("Preferred"), UNACCEPTABLE("Unacceptable");
  private String value;
  
  PreferenceLevel(String value) {
    this.value = value;
  }
  
  @JsonValue
  public String value() {
    return this.value;
  }
}