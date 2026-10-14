package com.caeca.estoque.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConviteSenhaRequestDto {

    @NotBlank
    @Size(min = 6, max = 20)
    @Schema(description = "Nova senha do usuário", example = "minhaSenha123")
    private String novaSenha;

    @NotBlank
    @Schema(description = "Confirmação da nova senha", example = "minhaSenha123")
    private String confirmarSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}
