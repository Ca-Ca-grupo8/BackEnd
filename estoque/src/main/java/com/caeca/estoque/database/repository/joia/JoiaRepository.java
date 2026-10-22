package com.caeca.estoque.database.repository.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.dto.joia.JoiaParadaDto;
import com.caeca.estoque.dto.joia.JoiaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JoiaRepository extends JpaRepository<Joia, Long> {
    @Query("""
    SELECT SUM(j.quantidade)
    FROM Joia as j
""")
    long somarTotalEstoque ();

    @Query("""
    SELECT j FROM Joia as j
    WHERE (:nome IS NULL OR LOWER(j.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
    AND (:categoriaId IS NULL OR j.categoria.id = :categoriaId)
    AND (:materialId IS null OR j.material.idMaterial = :materialId)
    AND (:banhoId IS NULL OR j.banho.idBanho = :banhoId)
    AND (cast(:dataInicio as date) IS NULL OR j.dataAtualizacao >= :dataInicio)
    AND (cast(:dataFim as date) IS NULL OR j.dataAtualizacao <= :dataFim)
    AND (
        cast(:status as string) IS NULL
        OR cast(:status as string) = (
            CASE
                WHEN j.isAtivo IS NULL OR j.isAtivo = false THEN 'INATIVO'
                WHEN j.quantidade IS NULL OR j.quantidade = 0 THEN 'CRITICO'
                WHEN j.quantidadeMinima IS NOT NULL AND j.quantidade <= j.quantidadeMinima THEN 'ALERTA'
                ELSE 'OK'
            END
        )
    )
""")
    Page<Joia> listarTodosFiltrado(
            String nome,
            Long categoriaId,
            Long materialId,
            Long banhoId,
            JoiaStatus status,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    @Query("""
    SELECT new com.caeca.estoque.dto.joia.JoiaParadaDto(
        j,
        COALESCE(j.quantidade, 0),
        CAST(COALESCE(j.preco, 0.0) * COALESCE(j.quantidade, 0) AS double),
        COALESCE(MAX(m.data), j.dataCadastro)
    )
    FROM Joia j
    LEFT JOIN Movimentacao m ON m.joia = j AND m.motivoMovimentacao.tipoMovimentacao.nome = 'SAIDA'
    WHERE j.dataCadastro IS NOT NULL
    GROUP BY j, j.id, j.quantidade, j.preco, j.dataCadastro
    HAVING (
        MAX(m.data) IS NOT NULL AND MAX(m.data) < :dataLimite
    ) OR (
        MAX(m.data) IS NULL AND j.dataCadastro < :dataLimite
    )
""")
    List<JoiaParadaDto> findJoiasParadas(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("""
    SELECT j FROM Joia j
    WHERE j.isAtivo = true
    AND j.quantidade <= j.quantidadeMinima
    ORDER BY (j.quantidade - j.quantidadeMinima) ASC
""")
    List<Joia> findAbaixoDoMinimo(Pageable pageable);
}
