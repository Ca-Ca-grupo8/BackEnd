package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.PerfilUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UsuarioPatchDto {

    @Size(min = 3, max = 10)
    @Schema(description = "Nome do usuário", example = "John Doe")
    private String nome;

    @Email
    @Schema(description = "Email do usuário", example = "john@doe.com")
    private String email;

    @Schema(description = "Perfil do usuário", example = "ROLE_USER")
    private PerfilUsuario perfilUsuario;

    @Schema(description = "Status do usuário", example = "ATIVO")
    private StatusUsuario status;

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

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }
}
