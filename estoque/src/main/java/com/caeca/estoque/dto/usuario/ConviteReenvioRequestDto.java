package com.caeca.estoque.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ConviteReenvioRequestDto {

    @NotBlank
    @Schema(description = "Token do convite expirado", example = "f3b8c2e0-1a2b-4c3d-9e8f-1234567890ab")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
