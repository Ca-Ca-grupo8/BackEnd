package com.caeca.estoque.database.repository.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
   List<Fornecedor> findByIsAtivo(Boolean isAtivo);

   List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

   List<Fornecedor> findByIsAtivoAndNomeContainingIgnoreCase(Boolean isAtivo, String nome);
}
