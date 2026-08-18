package com.courtreservation.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;
    
    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
    
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String userName;
    
    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String userMail;
    
    @Schema(description = "Rol del usuario", example = "USUARIO_FINAL")
    private String userRole;
}
