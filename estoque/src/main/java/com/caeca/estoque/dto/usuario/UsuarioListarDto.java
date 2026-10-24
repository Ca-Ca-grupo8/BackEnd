package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.PerfilUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UsuarioListarDto {

    @Schema(description = "Id do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "John Doe")
    private String nome;

    @Schema(description = "Email do usuário", example = "john@doe.com")
    private String email;

    @Schema(description = "Role do usuário", example = "ROLE_ADMIN")
    private PerfilUsuario role;

    @Schema(description = "Status do usuário", example = "ATIVO")
    private StatusUsuario status;

    @Schema(description = "Data de cadastro do usuário", example = "2026-10-24T10:15:30")
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização do usuário", example = "2026-10-25T08:00:00")
    private LocalDateTime dataAtualizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public PerfilUsuario getRole() {
        return role;
    }

    public void setRole(PerfilUsuario role) {
        this.role = role;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
