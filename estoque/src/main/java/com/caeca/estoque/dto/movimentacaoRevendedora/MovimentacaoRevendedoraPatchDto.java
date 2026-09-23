package com.caeca.estoque.dto.movimentacaoRevendedora;

public class MovimentacaoRevendedoraPatchDto {
    private boolean isFinalizada;

    public MovimentacaoRevendedoraPatchDto(boolean isFinalizada) {
        this.isFinalizada = isFinalizada;
    }

    public MovimentacaoRevendedoraPatchDto() {
    }

    public boolean isFinalizada() {
        return isFinalizada;
    }

    public void setFinalizada(boolean finalizada) {
        isFinalizada = finalizada;
    }
}
