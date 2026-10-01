package com.caeca.estoque.dto.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;

public class StatusMaletaResponseDto {

    private Long id;
    private StatusMaletaEnum nome;

    public StatusMaletaResponseDto() {
    }

    public StatusMaletaResponseDto(Long id, StatusMaletaEnum nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusMaletaEnum getNome() {
        return nome;
    }

    public void setNome(StatusMaletaEnum nome) {
        this.nome = nome;
    }
}
