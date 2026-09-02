package com.caeca.estoque.database.repository.movimentacao;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.dto.dashboard.GraficoMotivoDto;
import com.caeca.estoque.dto.dashboard.TopCincoDto;
import com.caeca.estoque.dto.dashboard.TopCincoMaisFriasDto;
import org.springframework.cglib.core.Local;
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
    """)
    Double somarValorTotalDasSaidas(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.TopCincoDto(
                m.joia.categoria.nome,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (
                    LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%'
                  )
            AND m.data BETWEEN :inicio AND :fim
            GROUP BY m.joia.categoria.nome
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<TopCincoDto> buscarTopCincoCategorias(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
            SELECT new com.caeca.estoque.dto.dashboard.TopCincoDto(
                m.joia.nome,
                SUM(m.quantidade))
            FROM Movimentacao m
            WHERE (
                    LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%'
                  )
            AND m.data BETWEEN :inicio AND :fim
            AND m.joia.categoria.id = :categoriaId
            GROUP BY m.joia.nome
            ORDER BY SUM(m.quantidade) DESC
    """)
    List<TopCincoDto> buscarTopCincoProdutos(LocalDateTime inicio, LocalDateTime fim, Long categoriaId, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%saida%')
            AND m.data BETWEEN :inicio AND :fim
    """)
    Long buscarTotalDeItensVendidos(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%entrada%')
            AND m.data BETWEEN :inicio AND :fim
    """)
    Long buscarTotalDeEntradas(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%entrada%')
            AND m.data > :fim
    """)
    long buscarTotalDeItensVendidosApos(LocalDateTime fim);

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0.0)
            FROM Movimentacao m
            WHERE (LOWER(m.motivoMovimentacao.tipoMovimentacao.nome) LIKE '%entrada%')
            AND m.data > :fim
    """)
    long buscarTotalDeEntradasApos(LocalDateTime fim);


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
        ORDER BY m.data DESC
    """)
    List<Movimentacao> buscarComFiltros(
            @Param("nome") String nome,
            @Param("tipo") TipoMovimentacaoEnum tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    Movimentacao findFirstByJoiaIdOrderByDataDesc(long id);

    @Query("""
            SELECT DISTINCT m.fornecedor
            FROM Movimentacao m
            WHERE m.joia.id = :joiaId
              AND m.fornecedor IS NOT NULL
    """)
    List<Fornecedor> buscarFornecedoresDaJoia(@Param("joiaId") Long joiaId);

    @Query("""
    SELECT new com.caeca.estoque.dto.dashboard.TopCincoMaisFriasDto(
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
        AND (:categoriaId = 0 OR j.categoria.id = :categoriaId)
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
    SELECT ROUND(
        100.0 * SUM(
            CASE
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM movimentacao m
                    WHERE m.joia_id = j.id
                      AND m.tipo_movimentacao_id_tipo_movimentacao = 2
                      AND m.data >= :dataInicio
                      AND m.data < :dataTermino
                )
                THEN 1
                ELSE 0
            END
        ) / COUNT(j.id),
        2
    )
    FROM joia j
    WHERE j.is_ativo = true
      AND (:categoriaId = 0 OR j.categoria_id = :categoriaId)
    """, nativeQuery = true)
    Number buscarIndiceAtual(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataTermino") LocalDateTime dataTermino,
            @Param("categoriaId") Long categoriaId
    );

    @Query(value = """
    SELECT ROUND(
        100.0 * SUM(
            CASE
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM movimentacao m
                    WHERE m.joia_id = j.id
                      AND m.tipo_movimentacao_id_tipo_movimentacao = 2
                      AND m.data >= :inicioMesAnterior
                      AND m.data < :fimMesAnterior
                )
                THEN 1
                ELSE 0
            END
        ) / COUNT(j.id),
        2
    )
    FROM joia j
    WHERE j.is_ativo = true
      AND (:categoriaId = 0 OR j.categoria_id = :categoriaId)
    """, nativeQuery = true)
    Number buscarIndiceMesAnterior(
            @Param("inicioMesAnterior") LocalDateTime inicioMesAnterior,
            @Param("fimMesAnterior") LocalDateTime fimMesAnterior,
            @Param("categoriaId") Long categoriaId
    );
}
