package com.caeca.estoque.database.repository.entradaXSaida;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComparacaoRepository extends JpaRepository<Movimentacao, Integer> {

    // Soma das Entradas
    @Query("SELECT COALESCE(SUM(m.quantidade), 0) FROM Movimentacao m " +
            "WHERE m.fkMovimentacao = 1 " +
            "  AND (:idJoia IS NULL OR m.fkJoia = :idJoia) " +
            "  AND (:dataInicio IS NULL OR m.data >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR m.data <= :dataFim)")
    Integer somarEntradas(
            @Param("idJoia") Long idJoia,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Soma das Saídas
    @Query("SELECT COALESCE(SUM(m.quantidade), 0) FROM Movimentacao m " +
            "WHERE m.fkMovimentacao = 2 " +
            "  AND (:idJoia IS NULL OR m.fkJoia = :idJoia) " +
            "  AND (:dataInicio IS NULL OR m.data >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR m.data <= :dataFim)")
    Integer somarSaidas(
            @Param("idJoia") Long idJoia,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Dados agrupados por mês para o gráfico de barras
    @Query("SELECT MONTH(m.data) AS mes, " +
            "COALESCE(SUM(CASE WHEN m.fkMovimentacao = 1 THEN m.quantidade ELSE 0 END), 0) AS entradas, " +
            "COALESCE(SUM(CASE WHEN m.fkMovimentacao = 2 THEN m.quantidade ELSE 0 END), 0) AS saidas " +
            "FROM Movimentacao m " +
            "WHERE (:ano IS NULL OR YEAR(m.data) = :ano) " +
            "  AND (:idJoia IS NULL OR m.fkJoia = :idJoia) " +
            "  AND (:dataInicio IS NULL OR m.data >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR m.data <= :dataFim) " +
            "GROUP BY MONTH(m.data) " +
            "ORDER BY MONTH(m.data)")
    List<MotivoMovimentacaoRequestDto> buscarDadosDoGrafico(
            @Param("ano") Integer ano,
            @Param("idJoia") Long idJoia,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}