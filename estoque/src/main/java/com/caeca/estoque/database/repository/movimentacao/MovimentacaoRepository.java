package com.caeca.estoque.database.repository.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
}
