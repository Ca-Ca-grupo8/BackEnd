package com.caeca.estoque.dto.usuario;

import com.caeca.estoque.database.model.usuario.Usuario;

public class ConviteValidacaoResponseDto {

    private String nome;
    private String email;

    public ConviteValidacaoResponseDto() {
    }

    public ConviteValidacaoResponseDto(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
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
}
