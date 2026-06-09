package com.caeca.estoque.database.repository.motivoMovimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotivoMovimentacaoRepository extends JpaRepository<MotivoMovimentacao, Integer> {

    boolean existsByDescricaoIgnoreCase(String descricao);
}
