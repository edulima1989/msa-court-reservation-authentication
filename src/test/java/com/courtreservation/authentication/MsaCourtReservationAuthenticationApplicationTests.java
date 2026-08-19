package com.courtreservation.authentication;

import com.courtreservation.authentication.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MsaCourtReservationAuthenticationApplicationTests {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  void contextLoads() {
  }

  @Test
  void generatesAndValidatesJwtWithConfiguredSecret() {
    String token = jwtTokenProvider.generateToken(1L, "user@example.com", "ADMIN");

    assertTrue(jwtTokenProvider.validateToken(token));
    assertEquals("user@example.com", jwtTokenProvider.extractUserMail(token));
    assertEquals(1L, jwtTokenProvider.extractUserId(token));
    assertEquals("ADMIN", jwtTokenProvider.extractUserRole(token));
  }

  @Test
  void allowsSwaggerEndpointsWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk());
  }

  @Test
  void appliesCorsForAllowedOrigins() throws Exception {
    mockMvc.perform(options("/api/auth/login")
                    .header(HttpHeaders.ORIGIN, "http://localhost:3000"))
            .andExpect(status().isOk())
            .andExpect(result -> assertEquals("http://localhost:3000",
                    result.getResponse().getHeader("Access-Control-Allow-Origin")));
  }

  @Test
  void rejectsCorsForDisallowedOrigins() throws Exception {
    mockMvc.perform(options("/api/auth/login")
                    .header(HttpHeaders.ORIGIN, "http://evil.example"))
            .andExpect(status().isForbidden());
  }

  @Test
  void allowsRegisterAndLoginWithoutAuthentication() throws Exception {
    String email = "user-" + UUID.randomUUID() + "@example.com";

    mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "userName": "Test User",
                              "userMail": "%s",
                              "userPassword": "password123",
                              "userRole": "USUARIO_FINAL"
                            }
                            """.formatted(email)))
            .andExpect(status().isCreated());

    mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "userMail": "%s",
                              "userPassword": "password123"
                            }
                            """.formatted(email)))
            .andExpect(status().isOk());
  }

  @Test
  void validatesSessionTokenWithoutAuthentication() throws Exception {
    String token = jwtTokenProvider.generateToken(55L, "validate@example.com", "USUARIO_FINAL");

    mockMvc.perform(post("/api/auth/validate-session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "token": "%s"
                            }
                            """.formatted(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.userId").value(55))
            .andExpect(jsonPath("$.userMail").value("validate@example.com"))
            .andExpect(jsonPath("$.userRole").value("USUARIO_FINAL"));
  }

  @Test
  void returnsInvalidWhenSessionTokenIsNotValid() throws Exception {
    mockMvc.perform(post("/api/auth/validate-session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "token": "token-invalido"
                            }
                            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.userId").doesNotExist())
            .andExpect(jsonPath("$.userMail").doesNotExist())
            .andExpect(jsonPath("$.userRole").doesNotExist());
  }

  @Test
  void rejectsProtectedEndpointsWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/auth/users"))
            .andExpect(status().isUnauthorized());
  }

  @Test
  void allowsProtectedEndpointsWithValidJwt() throws Exception {
    String token = jwtTokenProvider.generateToken(99L, "secured@example.com", "ADMIN");

    mockMvc.perform(get("/api/auth/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());
  }

}
