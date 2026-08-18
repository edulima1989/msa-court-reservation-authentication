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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
