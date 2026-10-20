package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.PerfilUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;

public class ConviteUsuarioResponseDto {

    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private StatusUsuario status;

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

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }
}
