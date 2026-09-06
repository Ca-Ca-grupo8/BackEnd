package com.caeca.estoque.dto.dashboard;

import java.math.BigDecimal;

public class IndicePecasFriasDto {

    private Double indiceAtual;
    private Double indiceMesAnterior;
    private Double diferenca;

    public IndicePecasFriasDto(Double indiceAtual, Double indiceMesAnterior, Double diferenca) {
        this.indiceAtual = indiceAtual;
        this.indiceMesAnterior = indiceMesAnterior;
        this.diferenca = diferenca;
    }

    public Double getIndiceAtual() {
        return indiceAtual;
    }

    public void setIndiceAtual(Double indiceAtual) {
        this.indiceAtual = indiceAtual;
    }

    public Double getIndiceMesAnterior() {
        return indiceMesAnterior;
    }

    public void setIndiceMesAnterior(Double indiceMesAnterior) {
        this.indiceMesAnterior = indiceMesAnterior;
    }

    public Double getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(Double diferenca) {
        this.diferenca = diferenca;
    }
}
