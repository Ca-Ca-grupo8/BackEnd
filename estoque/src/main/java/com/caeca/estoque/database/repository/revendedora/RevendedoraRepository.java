package com.caeca.estoque.database.repository.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RevendedoraRepository extends JpaRepository<Revendedora, Long> {
    boolean existsByEmail(String email);

    boolean existsByTelefone(String telefone);

    boolean existsByEnderecoId(Long enderecoId);

    Optional<Revendedora> findByEmail(String email);

    Optional<Revendedora> findByTelefone(String telefone);

    Optional<Revendedora> findByEnderecoId(Long enderecoId);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    boolean existsByEnderecoIdAndIdNot(Long enderecoId, Long id);

    @Query("SELECT r FROM Revendedora r WHERE r.dataNascimento IS NOT NULL "
            + "AND day(r.dataNascimento) = :day AND month(r.dataNascimento) = :month")
    List<Revendedora> findAniversariantes(int day, int month);

    List<Revendedora> findByAtivoTrue();

    @Query("SELECT r FROM Revendedora r WHERE EXISTS ("
            + "SELECT 1 FROM MovimentacaoRevendedora m "
            + "WHERE m.revendedora = r AND m.status.nome NOT IN :statusFechados)")
    Page<Revendedora> findComMaletaAberta(@Param("statusFechados") Collection<StatusMaletaEnum> statusFechados, Pageable pageable);
}
