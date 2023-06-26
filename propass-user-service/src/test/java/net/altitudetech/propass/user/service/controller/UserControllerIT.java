package net.altitudetech.propass.user.service.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.altitudetech.propass.commons.util.test.TokenUtils;
import net.altitudetech.propass.user.service.controller.config.PropassUserServiceTestConfig;
import net.altitudetech.propass.user.service.model.User;
import net.altitudetech.propass.user.service.service.UserService;

@DirtiesContext
@Sql("/import.sql")
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {PropassUserServiceTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {
  @LocalServerPort
  private int port;
  
  @Autowired
  private UserService userService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void testGetSingleUser() {
    int userId = 1;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(userId))
        .body("firstName", equalTo("John"))
        .body("lastName", equalTo("Doe"));
    //@formatter:on
  }
  
  @Test
  public void testGetSingleUserNoAuthorizationToken() {
    testForbidden("/users/1");
  }
  
  @Test
  public void testGetSingleUserNotFound() {
    int userId = 3;

    //@formatter:off
    given()
      .when()
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value());
    //@formatter:on
  }

  @Test
  public void testGetAllUsers() {
    //@formatter:off
    List<User> users = given()
      .when()
        .get("/users")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", User.class);
    //@formatter:on
    assertNotNull(users);
    assertEquals(2, users.size());
    assertTrue(users.stream().anyMatch(u -> u.getId().equals(1L)));
    assertTrue(users.stream().anyMatch(u -> u.getId().equals(2L)));
  }
  
  @Test
  public void testGetAllUsersFilteredByEmail() {
    String email = "john@doe.com";
    //@formatter:off
    List<User> users = given()
      .when()
        .queryParam("email", email)
        .header("Authorization", TokenUtils.VALID_TOKEN)
        .get("/users")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getList("content", User.class);
    //@formatter:on
    assertNotNull(users);
    assertEquals(1, users.size());
    User user = users.get(0);
    assertEquals(1L, user.getId());
    assertEquals(email, user.getEmail());
  }
  
  @Test
  public void testCreateUser() {
    //@formatter:off
    User user = User.builder()
        .firstName("John")
        .lastName("Constantine")
        .email("john@constantine.com")
        .password("123").build();

    Long id = given()
      .contentType(ContentType.JSON)
      .body(user)
      .when()
        .post("/users")
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .extract().body().jsonPath().getLong("id");
    //@formatter:on
    
    assertNotNull(id);
    
    User dbUser = this.userService.findOne(id).get();
    
    assertNotNull(dbUser);
    assertEquals(user.getFirstName(), dbUser.getFirstName());
    assertEquals(user.getLastName(), dbUser.getLastName());
    assertEquals(user.getEmail(), dbUser.getEmail());
    assertEquals(user.getPassword(), dbUser.getPassword());
    assertTrue(DateUtils.isSameDay(new Date(), dbUser.getCreatedAt()));
    assertTrue(DateUtils.isSameDay(new Date(), dbUser.getUpdatedAt()));
  }
  
  @Test
  public void testUpdateUser() {
    Long userId = 1L;
    
    //@formatter:off
    User user = User.builder()
        .firstName("John")
        .lastName("Constantine")
        .email("john@constantine.com")
        .password("123").build();

    given()
      .header("Authorization", TokenUtils.VALID_TOKEN)
      .contentType(ContentType.JSON)
      .body(user)
      .when()
        .put("/users/" + userId)
      .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.OK.value())
        .body("id", equalTo(userId.intValue()));
    //@formatter:on
    
    User dbUser = this.userService.findOne(userId).get();
    
    assertNotNull(dbUser);
    assertEquals(user.getFirstName(), dbUser.getFirstName());
    assertEquals(user.getLastName(), dbUser.getLastName());
    assertEquals(user.getEmail(), dbUser.getEmail());
    assertEquals(user.getPassword(), dbUser.getPassword());
    assertEquals("john@constantine.com", dbUser.getUpdatedBy());
    assertTrue(DateUtils.isSameDay(new Date(), dbUser.getUpdatedAt()));
  }
  
  @Test
  public void testUpdateUserNoAuthorizationToken() {
    testForbidden("/users/1", "PUT");
  }
  
  private void testForbidden(String url) {
    testForbidden(url, "GET");
  }
  
  private void testForbidden(String url, String method) {
    //@formatter:off
    RequestSpecification spec = given()
      .when();
    Response resp = null;
    
    switch (method) {
      case "POST": resp = spec.post(url); break;
      case "PUT": resp = spec.put(url); break;
      case "DELETE": resp = spec.delete(url); break;
      default: resp = spec.get(url); break;
    }
    
    resp.then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.FORBIDDEN.value());
    //@formatter:on
  }

}

