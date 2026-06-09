package com.caeca.estoque.database.repository.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMovimentacaoRepository extends JpaRepository<TipoMovimentacao, Long> {
}
