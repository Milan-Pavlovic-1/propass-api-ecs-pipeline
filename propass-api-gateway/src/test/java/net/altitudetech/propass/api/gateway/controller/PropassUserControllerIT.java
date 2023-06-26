package net.altitudetech.propass.api.gateway.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import net.altitudetech.propass.api.gateway.config.PropassAPIGatewayTestConfig;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.commons.util.test.TokenUtils;

@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassAPIGatewayTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropassUserControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleUser() throws JsonProcessingException {
    int userId = 1;
    
    //@formatter:off
    UserDTO user = UserDTO.builder()
      .firstName("John")
      .lastName("Constantine")
      .email("john@constantine.com")
      .build();
    
    stubFor(get(urlEqualTo("/users/" + userId))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(user))));
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/propass/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("firstName", equalTo(user.getFirstName()))
        .body("lastName", equalTo(user.getLastName()))
        .body("email", equalTo(user.getEmail()));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleUserNotFound() {
    int userId = 3;
    String errorMessage = "Test error";
    String errorBody = "{\"message\":\"" + errorMessage + "\"}";
    
    stubFor(get(urlEqualTo("/users/" + userId))
      .willReturn(aResponse()
        .withStatus(HttpStatus.NOT_FOUND.value())
        .withHeader("Content-Type", "application/json")
        .withHeader("Content-Length", "" + errorBody.getBytes().length)
        .withBody(errorBody)));

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/propass/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("message", equalTo(errorMessage));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleGenericError() {
    int userId = 5;
    String errorMessage = "Test error message";
    String errorBody = "{\"message\":\"" + errorMessage + "\"}";
    
    stubFor(get(urlEqualTo("/users/" + userId))
      .willReturn(aResponse()
        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
        .withHeader("Content-Type", "application/json")
        .withHeader("Content-Length", "" + errorBody.getBytes().length)
        .withBody(errorBody)));

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/propass/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("message", equalTo(errorMessage));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleUserNoAuthorizationToken() throws JsonProcessingException {
    //@formatter:off
    given()
      .when()
        .get("/propass/users/1")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }

}

