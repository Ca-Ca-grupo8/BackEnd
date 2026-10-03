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

    /**
     * Maletas com vencimento na janela informada, usada pela rotina diária para
     * checar os limites de alerta.
     */
    List<MovimentacaoRevendedora> findByDataLimiteBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Maletas com vencimento anterior ao instante informado, usada pela rotina
     * diária para detectar atrasos.
     */
    List<MovimentacaoRevendedora> findByDataLimiteBefore(LocalDateTime limite);
}
