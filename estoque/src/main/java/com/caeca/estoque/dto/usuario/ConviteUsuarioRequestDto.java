package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConviteUsuarioRequestDto {

    @NotBlank
    @Size(min = 3, max = 10)
    @Schema(description = "Nome do usuário", example = "Maria Souza")
    private String nome;

    @NotBlank
    @Email
    @Schema(description = "E-mail do usuário", example = "maria@empresa.com")
    private String email;

    @NotNull
    @Schema(description = "Perfil do usuário", example = "ROLE_USER")
    private PerfilUsuario perfilUsuario;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfilUsuario() {
        return perfilUsuario;
    }

    public void setPerfilUsuario(PerfilUsuario perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }
}
