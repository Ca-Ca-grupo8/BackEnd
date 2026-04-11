package com.caeca.estoque.database.repository.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovimentacaoRevendedoraRepository extends JpaRepository<MovimentacaoRevendedora, Long> {
    Optional<MovimentacaoRevendedora> findByRevendedoraIdRevendedoraAndIsFinalizadaFalse(long idRevendedora);

    List<MovimentacaoRevendedora> findByRevendedoraIdRevendedora();
}
