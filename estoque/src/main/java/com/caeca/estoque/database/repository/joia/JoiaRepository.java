package com.caeca.estoque.database.repository.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.dto.joia.JoiaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

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
}
