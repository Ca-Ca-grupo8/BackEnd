package com.caeca.estoque.database.repository.joia;

import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.dto.joia.JoiaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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
    ORDER BY j.dataAtualizacao DESC
""")
    List<Joia> listarTodosFiltrado(
            String nome,
            Long categoriaId,
            Long materialId,
            Long banhoId,
            LocalDate dataInicio,
            LocalDate dataFim
    );
}
