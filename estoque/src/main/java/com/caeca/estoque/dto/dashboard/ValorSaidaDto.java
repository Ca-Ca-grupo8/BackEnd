package com.caeca.estoque.dto.dashboard;

public class ValorSaidaDto {
    private Double valorAtual;
    private Double valorAntigo;
    private Double varianca;

    public ValorSaidaDto() {
    }

    public ValorSaidaDto(Double valorAtual, Double valorAntigo, Double varianca) {
        this.valorAtual = valorAtual;
        this.valorAntigo = valorAntigo;
        this.varianca = varianca;
    }

    public Double getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(Double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public Double getValorAntigo() {
        return valorAntigo;
    }

    public void setValorAntigo(Double valorAntigo) {
        this.valorAntigo = valorAntigo;
    }

    public Double getVarianca() {
        return varianca;
    }

    public void setVarianca(Double varianca) {
        this.varianca = varianca;
    }
}
