package net.altitudetech.propass.api.gateway.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
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
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.altitudetech.propass.api.gateway.config.PropassAPIGatewayTestConfig;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.api.gateway.dto.LoginRegisterResponseDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.commons.util.test.TokenUtils;

@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassAPIGatewayTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropassAuthControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testLogin() throws JsonProcessingException {
    String token = "123";
    String email = "john@constantine.com";
    String password = "password";
    
    //@formatter:off
    LoginRegisterRequestDTO request = LoginRegisterRequestDTO.builder()
        .email(email)
        .password(password)
        .build();
    LoginRegisterResponseDTO response = LoginRegisterResponseDTO.builder()
        .token(token)
        .build();
    
    stubFor(post(urlEqualTo("/auth/login"))
        .withRequestBody(matchingJsonPath("$.email", WireMock.equalTo(email)))
        .withRequestBody(matchingJsonPath("$.password", WireMock.equalTo(password)))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(response))));
    
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/propass/auth/login")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("token", equalTo(token));
    //@formatter:on
  }
  
  @Test
  public void testLoginNoSuchEmail() throws JsonProcessingException {
    String errorMessage = "No such email.";
    String errorBody = "{\"message\":\"" + errorMessage + "\"}";
    
    //@formatter:off
    stubFor(post(urlEqualTo("/auth/login"))
      .willReturn(aResponse()
        .withStatus(HttpStatus.FORBIDDEN.value())
        .withHeader("Content-Type", "application/json")
        .withHeader("Content-Length", "" + errorBody.getBytes().length)
        .withBody(errorBody)));
    
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(new LoginRegisterRequestDTO())
        .post("/propass/auth/login")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("message", equalTo(errorMessage));
    //@formatter:on
  }
  
  @Test
  public void testRegister() throws JsonProcessingException {
    String token = "123";
    String email = "john@constantine.com";
    String password = "password";
    
    //@formatter:off
    LoginRegisterRequestDTO request = LoginRegisterRequestDTO.builder()
        .email(email)
        .password(password)
        .build();
    LoginRegisterResponseDTO response = LoginRegisterResponseDTO.builder()
        .token(token)
        .build();
    
    stubFor(post(urlEqualTo("/auth/register"))
        .withRequestBody(matchingJsonPath("$.email", WireMock.equalTo(email)))
        .withRequestBody(matchingJsonPath("$.password", WireMock.equalTo(password)))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(response))));
    
    given()
      .when()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/propass/auth/register")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("token", equalTo(token));
    //@formatter:on
  }
  
  @Test
  public void testMe() throws JsonProcessingException {
    //@formatter:off
    UserDTO user = UserDTO.builder()
      .firstName("John")
      .lastName("Constantine")
      .email("john@constantine.com")
      .build();
    
    stubFor(get(urlEqualTo("/auth/me"))
      .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(user))));
    
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/propass/auth/me")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("firstName", equalTo(user.getFirstName()))
        .body("lastName", equalTo(user.getLastName()))
        .body("email", equalTo(user.getEmail()));
    //@formatter:on
  }
  
  @Test
  public void testMeWithoutAuthorizationHeader() {
    //@formatter:off
    given()
      .when()
        .get("/propass/auth/me")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
}

