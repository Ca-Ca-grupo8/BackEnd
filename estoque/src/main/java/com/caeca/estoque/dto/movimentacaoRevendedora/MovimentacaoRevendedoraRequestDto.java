package com.caeca.estoque.dto.movimentacaoRevendedora;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class MovimentacaoRevendedoraRequestDto {
    @Schema(example = "1", description = "Id da revendedora")
    @NotNull
    private Long idRevendedora;

    @Schema(example = "2026-12-31T00:00:00", description = "Data limite da movimentação")
    private LocalDateTime dataLimite;

    public MovimentacaoRevendedoraRequestDto(Long idRevendedora) {
        this.idRevendedora = idRevendedora;
    }

    public MovimentacaoRevendedoraRequestDto(Long idRevendedora, LocalDateTime dataLimite) {
        this.idRevendedora = idRevendedora;
        this.dataLimite = dataLimite;
    }

    public Long getIdRevendedora() {
        return idRevendedora;
    }

    public void setIdRevendedora(Long idRevendedora) {
        this.idRevendedora = idRevendedora;
    }

    public LocalDateTime getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDateTime dataLimite) {
        this.dataLimite = dataLimite;
    }
}
