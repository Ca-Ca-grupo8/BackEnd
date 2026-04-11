package com.caeca.estoque.database.model.movimentacaoRevendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import jakarta.persistence.*;

@Entity
public class MovimentecaoRevendedora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimentacaoRevendedora;
    private boolean isFinalizada;

    @ManyToOne
    private Revendedora revendedora;

    public MovimentecaoRevendedora() {
    }

    public MovimentecaoRevendedora(Long idMovimentacaoRevendedora, boolean isFinalizada, Revendedora revendedora) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
        this.isFinalizada = isFinalizada;
        this.revendedora = revendedora;
    }

    public Long getIdMovimentacaoRevendedora() {
        return idMovimentacaoRevendedora;
    }

    public void setIdMovimentacaoRevendedora(Long idMovimentacaoRevendedora) {
        this.idMovimentacaoRevendedora = idMovimentacaoRevendedora;
    }

    public boolean isFinalizada() {
        return isFinalizada;
    }

    public void setFinalizada(boolean finalizada) {
        isFinalizada = finalizada;
    }

    public Revendedora getRevendedora() {
        return revendedora;
    }

    public void setRevendedora(Revendedora revendedora) {
        this.revendedora = revendedora;
    }
}
