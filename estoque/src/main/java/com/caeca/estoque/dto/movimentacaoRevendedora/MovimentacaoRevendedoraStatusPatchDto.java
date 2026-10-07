package com.caeca.estoque.dto.movimentacaoRevendedora;

import jakarta.validation.constraints.NotNull;

public class MovimentacaoRevendedoraStatusPatchDto {

    @NotNull(message = "O id do status da maleta é obrigatório")
    private Long idStatus;

    public MovimentacaoRevendedoraStatusPatchDto() {
    }

    public MovimentacaoRevendedoraStatusPatchDto(Long idStatus) {
        this.idStatus = idStatus;
    }

    public Long getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }
}
