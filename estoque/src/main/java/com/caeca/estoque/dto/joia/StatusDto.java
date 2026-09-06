package com.caeca.estoque.dto.joia;

public class StatusDto {
    private Boolean ativo;

    public StatusDto(Boolean ativo) {
        this.ativo = ativo;
    }

    public StatusDto() {}

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}


