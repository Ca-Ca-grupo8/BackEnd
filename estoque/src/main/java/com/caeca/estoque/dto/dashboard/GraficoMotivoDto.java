package com.caeca.estoque.dto.dashboard;

public class GraficoMotivoDto {
    private String motivo;
    private Long quantidade;

    public GraficoMotivoDto() {
    }

    public GraficoMotivoDto(String motivo, Long quantidade) {
        this.motivo = motivo;
        this.quantidade = quantidade;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}