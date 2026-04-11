package com.caeca.estoque.database.repository;

import com.caeca.estoque.database.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMovimentacaoRepository extends JpaRepository<TipoMovimentacao, Integer> {

    boolean existsByDescricaoIgnoreCase(String descricao);
}
