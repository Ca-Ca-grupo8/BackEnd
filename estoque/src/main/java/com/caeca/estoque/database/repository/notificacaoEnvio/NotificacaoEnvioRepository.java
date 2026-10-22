package com.caeca.estoque.database.repository.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.NotificacaoEnvio;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificacaoEnvioRepository extends JpaRepository<NotificacaoEnvio, Long> {
    boolean existsByTipoReferenciaAndReferenciaIdAndTemplateIdAndPermiteReenvioFalse(TipoReferencia tipoReferencia, long referenciaId, long templateId);

    @Query("""
    SELECT n FROM NotificacaoEnvio n
    WHERE (:tipoReferencia IS NULL OR n.tipoReferencia = :tipoReferencia)
    AND (:templateId IS NULL OR n.templateId = :templateId)
    ORDER BY n.dataEnvio DESC, n.idNotificacaoEnvio DESC
""")
    Page<NotificacaoEnvio> buscarEnvios(
            @Param("tipoReferencia") TipoReferencia tipoReferencia,
            @Param("templateId") Long templateId,
            Pageable pageable
    );
}
