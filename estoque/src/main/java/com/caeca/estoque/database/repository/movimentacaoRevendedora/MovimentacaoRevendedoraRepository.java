package com.caeca.estoque.database.repository.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoRevendedoraRepository extends JpaRepository<MovimentacaoRevendedora, Long> {
    Optional<MovimentacaoRevendedora> findByRevendedoraIdAndStatusNome(long idRevendedora, StatusMaletaEnum status);

    List<MovimentacaoRevendedora> findByRevendedoraId(long idRevendedora);

    List<MovimentacaoRevendedora> findByDataLimiteBetween(LocalDateTime inicio, LocalDateTime fim);

    List<MovimentacaoRevendedora> findByDataLimiteBefore(LocalDateTime limite);

    MovimentacaoRevendedora findFirstByRevendedoraIdOrderByIdMovimentacaoRevendedoraDesc(Long id);

    Optional<MovimentacaoRevendedora> findByRevendedoraIdAndStatusNomeNot(Long idRevendedora, StatusMaletaEnum statusMaletaEnum);
}
