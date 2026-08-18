package com.courtreservation.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String userMail;
    
    @Schema(description = "Contraseña del usuario", example = "password123")
    private String userPassword;
}
