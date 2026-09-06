package com.caeca.estoque.database.repository.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMovimentacaoRepository extends JpaRepository<TipoMovimentacao, Long> {
}
