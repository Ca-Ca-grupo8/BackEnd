package com.caeca.estoque.dto.joia;

public class StatusDTO {
    private Boolean ativo;

    public StatusDTO(Boolean ativo) {
        this.ativo = ativo;
    }

    public StatusDTO() {}

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}


