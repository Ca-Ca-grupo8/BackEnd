package com.caeca.estoque.database.repository.movimentacao;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.dto.dashboard.GraficoMotivoDto;
import com.caeca.estoque.dto.dashboard.TopCincoDto;
import com.caeca.estoque.dto.dashboard.TopCincoMaisFriasDto;
import com.caeca.estoque.dto.joia.JoiaParadaDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.JoiaSaidaMaletaDto;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    @Query("""
            SELECT COALESCE(SUM(m.precoSaida), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    Double somarValorTotalDasSaidas(LocalDateTime inicio, LocalDateTime fim,
                                    @Param("categoriaId") Long categoriaId);

    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.TopCincoDto(
                m.joia.categoria.id,
                m.joia.categoria.nome,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (
                    LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%'
                  )
            AND m.data BETWEEN :inicio AND :fim
            GROUP BY m.joia.categoria.id, m.joia.categoria.nome
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<TopCincoDto> buscarTopCincoCategorias(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.TopCincoDto(
                m.joia.id,
                m.joia.nome,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (
                    LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%'
                  )
            AND m.data BETWEEN :inicio AND :fim
            AND m.joia.categoria.id = :categoriaId
            GROUP BY m.joia.id, m.joia.nome
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<TopCincoDto> buscarTopCincoProdutos(LocalDateTime inicio, LocalDateTime fim, Long categoriaId, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    Long buscarTotalDeItensVendidos(LocalDateTime inicio, LocalDateTime fim,
                                    @Param("categoriaId") Long categoriaId);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%entrada%')
            AND m.data BETWEEN :inicio AND :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    Long buscarTotalDeEntradas(LocalDateTime inicio, LocalDateTime fim,
                               @Param("categoriaId") Long categoriaId);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data > :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    long buscarTotalDeItensVendidosApos(LocalDateTime fim,
                                        @Param("categoriaId") Long categoriaId);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%entrada%')
            AND m.data > :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    long buscarTotalDeEntradasApos(LocalDateTime fim,
                                   @Param("categoriaId") Long categoriaId);


    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.GraficoMotivoDto(
                m.motivoMovimentacao.descricao,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
            GROUP BY m.motivoMovimentacao.descricao
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<GraficoMotivoDto> buscarGraficoMotivosSaida(LocalDateTime inicio, LocalDateTime fim, Long categoriaId);

    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.GraficoMotivoDto(
                m.motivoMovimentacao.descricao,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%reentrada%')
            AND m.data BETWEEN :inicio AND :fim
            AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
            GROUP BY m.motivoMovimentacao.descricao
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<GraficoMotivoDto> buscarGraficoMotivosReentrada(LocalDateTime inicio, LocalDateTime fim, Long categoriaId);

    @Query("""
        SELECT m FROM Movimentacao m
        WHERE (:nome IS NULL OR LOWER(m.joia.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:tipo IS NULL OR m.motivoMovimentacao.tipoMovimentacao.nome = :tipo)
          AND (cast(:dataInicio as date) IS NULL OR m.data >= :dataInicio)
          AND (cast(:dataFim as date) IS NULL OR m.data <= :dataFim)
    """)
    Page<Movimentacao> buscarComFiltros(
            @Param("nome") String nome,
            @Param("tipo") TipoMovimentacaoEnum tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
            SELECT new com.caeca.estoque.dto.movimentacaoRevendedora.JoiaSaidaMaletaDto(
                m.joia.id,
                m.joia.nome,
                SUM(CASE
                        WHEN m.motivoMovimentacao.tipoMovimentacao.nome = 'SAIDA' THEN m.quantidade
                        WHEN m.motivoMovimentacao.tipoMovimentacao.nome = 'REENTRADA' THEN -m.quantidade
                        ELSE 0
                    END))
            FROM Movimentacao m
            WHERE m.movimentacaoRevendedora.id = :idMaleta
            GROUP BY m.joia.id, m.joia.nome
            HAVING SUM(CASE
                        WHEN m.motivoMovimentacao.tipoMovimentacao.nome = 'SAIDA' THEN m.quantidade
                        WHEN m.motivoMovimentacao.tipoMovimentacao.nome = 'REENTRADA' THEN -m.quantidade
                        ELSE 0
                    END) > 0
            ORDER BY m.joia.nome
    """)
    List<JoiaSaidaMaletaDto> buscarJoiasComSaidaNaMaleta(@Param("idMaleta") Long idMaleta);

    @Query("""
            SELECT new com.caeca.estoque.database.repository.movimentacao.JoiaSaidaProjecao(
                m.joia.id,
                m.joia.nome,
                SUM(m.quantidade),
                AVG(m.quantidade))
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
            GROUP BY m.joia.id
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<JoiaSaidaProjecao> buscarJoiasMaisSaidas(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
            SELECT new com.caeca.estoque.database.repository.movimentacao.MotivoSaidaJoiaProjecao(
                m.joia.id,
                m.motivoMovimentacao.idMotivoMovimentacao,
                m.motivoMovimentacao.descricao,
                COUNT(m))
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
            AND m.joia.id IN :joiaIds
            GROUP BY m.joia.id, m.motivoMovimentacao.idMotivoMovimentacao, m.motivoMovimentacao.descricao
    """)
    List<MotivoSaidaJoiaProjecao> buscarMotivosSaidaDasJoias(LocalDateTime inicio, LocalDateTime fim, List<Long> joiaIds);

    Movimentacao findFirstByJoiaIdOrderByDataDesc(long id);

    @Query("""
            SELECT DISTINCT m.fornecedor
            FROM Movimentacao m
            WHERE m.joia.id = :joiaId
              AND m.fornecedor IS NOT NULL
    """)
    List<Fornecedor> buscarFornecedoresDaJoia(@Param("joiaId") Long joiaId);

    @Query("""
            SELECT new com.caeca.estoque.database.repository.movimentacao.FornecedorJoiaProjecao(
                m.joia.id, m.fornecedor)
            FROM Movimentacao m
            WHERE m.joia.id IN :joiaIds
              AND m.fornecedor IS NOT NULL
    """)
    List<FornecedorJoiaProjecao> buscarFornecedoresDasJoias(@Param("joiaIds") List<Long> joiaIds);

    @Query("""
            SELECT j FROM Joia j
            WHERE j.id IN (
                SELECT m.joia.id FROM Movimentacao m
                WHERE m.fornecedor.id = :fornecedorId
            )
    """)
    Page<Joia> buscarJoiasDoFornecedor(@Param("fornecedorId") Long fornecedorId, Pageable pageable);

    @Query("""
    SELECT new com.caeca.estoque.dto.dashboard.TopCincoMaisFriasDto(
        j.id,
        j.nome,
        j.quantidade,
        MAX(m.data)
    )
    FROM Joia j
    LEFT JOIN Movimentacao m
        ON m.joia.id = j.id
        AND m.motivoMovimentacao.tipoMovimentacao.id = 2
        AND m.data BETWEEN :inicio AND :fim
    WHERE j.isAtivo = true
        AND (:categoriaId IS NULL OR j.categoria.id = :categoriaId)
    GROUP BY j.id, j.nome, j.quantidade
    ORDER BY
        CASE WHEN MAX(m.data) IS NULL THEN 0 ELSE 1 END,
        MAX(m.data) ASC
    """)
    List<TopCincoMaisFriasDto> buscarProdutosFrios(
            LocalDateTime inicio,
            LocalDateTime fim,
            Long categoriaId,
            Pageable pageable
    );

    @Query(value = """
    SELECT COALESCE(ROUND(
        100.0 * SUM(
            CASE
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM movimentacao m
                    JOIN motivo_movimentacao mm
                      ON mm.id_motivo_movimentacao = m.motivo_movimentacao_id_motivo_movimentacao
                    JOIN tipo_movimentacao tm
                      ON tm.id = mm.tipo_movimentacao_id
                    WHERE m.joia_id = j.id
                      AND lower(tm.nome) LIKE '%saida%'
                      AND m.data >= :dataInicio
                      AND m.data < :dataTermino
                )
                THEN 1
                ELSE 0
            END
        ) / NULLIF(COUNT(j.id), 0),
        2
    ), 0)
    FROM joia j
    WHERE j.is_ativo = true
      AND (:categoriaId IS NULL OR j.categoria_id = :categoriaId)
    """, nativeQuery = true)
    Number buscarIndiceAtual(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataTermino") LocalDateTime dataTermino,
            @Param("categoriaId") Long categoriaId
    );

    @Query(value = """
    SELECT COALESCE(ROUND(
        100.0 * SUM(
            CASE
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM movimentacao m
                    JOIN motivo_movimentacao mm
                      ON mm.id_motivo_movimentacao = m.motivo_movimentacao_id_motivo_movimentacao
                    JOIN tipo_movimentacao tm
                      ON tm.id = mm.tipo_movimentacao_id
                    WHERE m.joia_id = j.id
                      AND lower(tm.nome) LIKE '%saida%'
                      AND m.data >= :inicioMesAnterior
                      AND m.data < :fimMesAnterior
                )
                THEN 1
                ELSE 0
            END
        ) / NULLIF(COUNT(j.id), 0),
        2
    ), 0)
    FROM joia j
    WHERE j.is_ativo = true
      AND (:categoriaId IS NULL OR j.categoria_id = :categoriaId)
    """, nativeQuery = true)
    Number buscarIndiceMesAnterior(
            @Param("inicioMesAnterior") LocalDateTime inicioMesAnterior,
            @Param("fimMesAnterior") LocalDateTime fimMesAnterior,
            @Param("categoriaId") Long categoriaId
    );
}