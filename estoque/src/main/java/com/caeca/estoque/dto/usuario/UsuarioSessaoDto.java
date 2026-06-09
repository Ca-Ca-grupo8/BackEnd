package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.PerfilUsuario;

public class UsuarioSessaoDto {

    private Long userId;
    private String nome;
    private String email;
    private PerfilUsuario role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
