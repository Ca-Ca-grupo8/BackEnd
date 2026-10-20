package com.caeca.estoque.database.model.historicoEstoque;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Foto do total de peças em estoque em uma data.
 *
 * A quantidade de cada joia muda por caminhos que não deixam rastro (cadastro
 * direto de joia, por exemplo), então não dá pra reconstruir o passado somando
 * movimentações. Por isso guardamos um snapshot diário para conseguir comparar
 * o estoque atual com o de períodos anteriores na dashboard.
 */
@Entity
@Table(name = "historico_estoque")
public class HistoricoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data", nullable = false, unique = true)
    private LocalDate data;

    @Column(name = "total_estoque", nullable = false)
    private Long totalEstoque;

    public HistoricoEstoque() {
    }

    public HistoricoEstoque(LocalDate data, Long totalEstoque) {
        this.data = data;
        this.totalEstoque = totalEstoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getTotalEstoque() {
        return totalEstoque;
    }

    public void setTotalEstoque(Long totalEstoque) {
        this.totalEstoque = totalEstoque;
    }
}
