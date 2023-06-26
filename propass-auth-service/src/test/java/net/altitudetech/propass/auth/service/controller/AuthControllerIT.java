package net.altitudetech.propass.auth.service.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.altitudetech.propass.auth.service.config.PropassAuthServiceTestConfig;
import net.altitudetech.propass.auth.service.dto.LoginRegisterRequestDTO;
import net.altitudetech.propass.auth.service.dto.UserDTO;
import net.altitudetech.propass.commons.util.test.TokenUtils;

@ContextConfiguration
@AutoConfigureWireMock(port = 8085)
@SpringBootTest(classes = {PropassAuthServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIT {
  private static final Long TEST_USER_ID = 123L;

  @LocalServerPort
  private int port;
  
  @Value("${propass.security.jwt.secret:}")
  private String secret;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
    WireMock.reset();
  }

  @Test
  public void testLogin() throws JsonProcessingException {
    String email = "john@constantine.com";
    String password = "password";

    stubForEmailAndPassword(email, password);
    
    //@formatter:off
    String token = given()
      .when()
        .body(LoginRegisterRequestDTO.builder().email(email).password(password).build())
        .contentType(ContentType.JSON)
        .post("/auth/login")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasKey("token"))
        .extract().body().jsonPath().getString("token");
    //@formatter:on
    
    assertEquals(email, getEmail(token));
  }
  
  @Test
  public void testLoginWrongEmail() throws JsonProcessingException {
    String email = "john@constantine.com";
    stubForUserList(email, Collections.emptyList());
    
    //@formatter:off
    given()
      .when()
        .body(LoginRegisterRequestDTO.builder().email(email).password("password").build())
        .contentType(ContentType.JSON)
        .post("/auth/login")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  @Test
  public void testLoginWrongPassword() throws JsonProcessingException {
    String email = "john@constantine.com";
    String password = "password";

    stubForEmailAndPassword(email, password);
    
    //@formatter:off
    given()
      .when()
        .body(LoginRegisterRequestDTO.builder().email(email).password("pa$$word").build())
        .contentType(ContentType.JSON)
        .post("/auth/login")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }

  @Test
  public void testRegister() throws JsonProcessingException {
    String email = "john@constantine.com";
    String password = "password";

    //@formatter:off
    UserDTO user = getUser(email, password);
    
    stubFor(post(urlMatching("/users"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(user))));
    
    String token = given()
      .when()
        .body(LoginRegisterRequestDTO.builder().email(email).password(password).build())
        .contentType(ContentType.JSON)
        .post("/auth/register")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasKey("token"))
        .extract().body().jsonPath().getString("token");
    //@formatter:on
    
    assertEquals(email, getEmail(token));
  }
  
  @Test
  public void testRegisterExistingUser() throws JsonProcessingException {
    String email = "john@constantine.com";
    String password = "password";

    stubForEmailAndPassword(email, password);
    
    //@formatter:off
    given()
      .when()
        .body(LoginRegisterRequestDTO.builder().email(email).password(password).build())
        .contentType(ContentType.JSON)
        .post("/auth/register")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  @Test
  public void testMe() throws JsonProcessingException {
    String email = "john@constantine.com";
    String password = "password";

    stubForEmailAndPassword(email, password);
    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/auth/me")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(TEST_USER_ID.intValue()))
        .body("firstName", equalTo("John"))
        .body("lastName", equalTo("Constantine"));
    //@formatter:on
  }
  
  @Test
  public void testMeWithoutToken() throws JsonProcessingException {
    //@formatter:off
    given()
      .when()
        .get("/auth/me")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  @Test
  public void testMeWithInvalidToken() throws JsonProcessingException {
    //@formatter:off
    given()
      .when()
        .header("Authorization", "123")
        .get("/auth/me")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }
  
  private void stubForEmailAndPassword(String email, String password) throws JsonProcessingException {
    stubForUserList(email, Arrays.asList(getUser(email, password)));
  }
  
  private void stubForUserList(String email, List<UserDTO> list) throws JsonProcessingException {
    //@formatter:off
    stubFor(get(urlMatching("/users.*"))
      .withQueryParam("email", WireMock.equalTo(email))
      .withQueryParam("company", WireMock.equalTo("0"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(this.objectMapper.writeValueAsString(
            new PageImpl<>(list)))));
    //@formatter:on
  }
  
  private UserDTO getUser(String email, String password) {
    UserDTO user = UserDTO.builder()
      .firstName("John")
      .lastName("Constantine")
      .email(email)
      .password(this.passwordEncoder.encode(password))
      .build();
    user.setId(TEST_USER_ID);
    return user;
  }
  
  private String getEmail(String token) {
    byte[] keyBytes = Decoders.BASE64.decode(this.secret);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
        .getSubject();
  }

}

