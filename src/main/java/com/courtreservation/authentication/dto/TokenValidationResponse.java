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
public class TokenValidationResponse {

    @Schema(description = "Indica si el token es válido", example = "true")
    private boolean valid;

    @Schema(description = "ID del usuario en el token cuando es válido", example = "1")
    private Long userId;

    @Schema(description = "Email del usuario en el token cuando es válido", example = "juan@example.com")
    private String userMail;

    @Schema(description = "Rol del usuario en el token cuando es válido", example = "ADMIN")
    private String userRole;
}
