package com.caeca.estoque.database.repository.tipoMovimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository; // <-- Importante!
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoDashboardRepository extends Repository<Movimentacao, Long> {

    @Query("""
        SELECT COALESCE(SUM(m.quantidade), 0) 
        FROM Movimentacao m
        WHERE m.motivoMovimentacao.tipoMovimentacao.nome IN :tipos
        AND m.data >= :dataInicio 
        AND m.data <= :dataFim
        AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
    """)
    long somarQuantidadePorTipo(
            @Param("tipos") List<TipoMovimentacaoEnum> tipos,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("categoriaId") Long categoriaId
    );

    // Retorna um array de objetos por linha: [String mes (yyyy-MM), Long entradas, Long saidas]
    @Query("""
        SELECT 
            FUNCTION('DATE_FORMAT', m.data, '%Y-%m'),
            SUM(CASE WHEN m.motivoMovimentacao.tipoMovimentacao.nome IN ('ENTRADA', 'REENTRADA') THEN m.quantidade ELSE 0 END),
            SUM(CASE WHEN m.motivoMovimentacao.tipoMovimentacao.nome = 'SAIDA' THEN m.quantidade ELSE 0 END)
        FROM Movimentacao m
        WHERE m.data >= :dataInicio AND m.data <= :dataFim
        AND (:categoriaId IS NULL OR m.joia.categoria.id = :categoriaId)
        GROUP BY FUNCTION('DATE_FORMAT', m.data, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', m.data, '%Y-%m')
    """)
    List<Object[]> buscarDadosParaGrafico(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("categoriaId") Long categoriaId
    );

}