package com.caeca.estoque.database.repository.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

interface movimentacaoRevendedoraRepository extends JpaRepository<Movimentacao, Long> {
}
