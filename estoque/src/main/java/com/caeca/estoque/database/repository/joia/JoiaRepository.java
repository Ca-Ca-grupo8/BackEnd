package com.caeca.estoque.database.repository.joia;

import com.caeca.estoque.database.model.joia.Joia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JoiaRepository extends JpaRepository<Joia, Long> {
    @Query("""
    SELECT SUM(j.quantidade)
    FROM Joia as j
""")
    long somarTotalEstoque ();
}
