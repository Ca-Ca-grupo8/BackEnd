package com.caeca.estoque.dto.DashEntradaXSaida;

public class DashEntradaXSaidaDto {

    private Integer totalEntradas;
    private Integer totalSaidas;
    private Integer produtosEmEstoque;

    public DashEntradaXSaidaDto(Integer totalEntradas, Integer totalSaidas, Integer produtosEmEstoque) {
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.produtosEmEstoque = produtosEmEstoque;
    }


    public Integer getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(Integer totalEntradas) { this.totalEntradas = totalEntradas; }

    public Integer getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(Integer totalSaidas) { this.totalSaidas = totalSaidas; }

    public Integer getProdutosEmEstoque() { return produtosEmEstoque; }
    public void setProdutosEmEstoque(Integer produtosEmEstoque) { this.produtosEmEstoque = produtosEmEstoque; }
}

