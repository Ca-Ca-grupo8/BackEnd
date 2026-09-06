package com.caeca.estoque.dto.movimentacaoRevendedora;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class MovimentacaoRevendedoraRequestDto {
    @Schema(example = "1", description = "Id da revendedora")
    @NotNull
    private Long idRevendedora;

    public MovimentacaoRevendedoraRequestDto(Long idRevendedora) {
        this.idRevendedora = idRevendedora;
    }

    public Long getIdRevendedora() {
        return idRevendedora;
    }

    public void setIdRevendedora(Long idRevendedora) {
        this.idRevendedora = idRevendedora;
    }
}
