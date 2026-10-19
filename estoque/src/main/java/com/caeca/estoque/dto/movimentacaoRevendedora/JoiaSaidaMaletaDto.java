package com.caeca.estoque.dto.movimentacaoRevendedora;

import io.swagger.v3.oas.annotations.media.Schema;

public class JoiaSaidaMaletaDto {

    @Schema(example = "1", description = "Identificador da joia")
    private Long id;

    @Schema(example = "Colar prata folheado", description = "Nome da joia")
    private String nome;

    @Schema(example = "3", description = "Quantidade líquida de saída (saídas menos reentradas)")
    private Long quantidade;

    public JoiaSaidaMaletaDto() {
    }

    public JoiaSaidaMaletaDto(Long id, String nome, Long quantidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
