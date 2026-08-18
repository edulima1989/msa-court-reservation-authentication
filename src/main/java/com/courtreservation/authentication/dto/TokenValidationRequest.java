package com.courtreservation.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationRequest {

    @Schema(description = "Token JWT de sesión", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;
}
