package com.caeca.estoque.database.repository.joia;

import com.caeca.estoque.database.model.joia.Joia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JoiaRepository extends JpaRepository<Joia, Long> {
    @Query("""
    SELECT SUM(j.quantidade)
    FROM Joia as j
""")
    long somarTotalEstoque ();

    @Query(value = """
        SELECT
            j.id,
            j.nome,
            MAX(m.data),
            j.quantidade
        FROM joia j
        LEFT JOIN movimentacao m
            ON m.joia_id = j.id
            AND m.tipo_movimentacao_id_tipo_movimentacao = 2
        WHERE j.is_ativo = 1
        GROUP BY
            j.id,
            j.nome,
            j.quantidade
        ORDER BY
            CASE WHEN MAX(m.data) IS NULL THEN 0 ELSE 1 END,
            MAX(m.data) ASC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> buscarProdutosFrios();
}
