package com.caeca.estoque.database.repository.historicoEstoque;

import com.caeca.estoque.database.model.historicoEstoque.HistoricoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HistoricoEstoqueRepository extends JpaRepository<HistoricoEstoque, Long> {

    Optional<HistoricoEstoque> findByData(LocalDate data);

    Optional<HistoricoEstoque> findTopByDataLessThanEqualOrderByDataDesc(LocalDate data);
}
