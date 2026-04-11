package com.caeca.estoque.dto.movimentacaoRevendedora;

public class MovimentacaoRevendedoraRequestDto {
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
