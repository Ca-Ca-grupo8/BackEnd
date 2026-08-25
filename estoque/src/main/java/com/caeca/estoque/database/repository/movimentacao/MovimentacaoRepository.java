package com.caeca.estoque.database.repository.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.dashboard.TopCincoDto;
import com.caeca.estoque.dto.dashboard.TopCincoMaisFriasDto;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
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
}
