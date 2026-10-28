package com.caeca.estoque.database.repository.revendedora;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.endereco.EnderecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercita a query de aniversario contra o H2 de verdade, porque JPQL so e validado
 * na primeira invocacao. Um teste com repository mockado nao pegaria query invalida.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:h2:mem:revendedora-repository-test;DB_CLOSE_DELAY=-1;MODE=MySQL")
class RevendedoraRepositoryTest {

    @Autowired
    private RevendedoraRepository revendedoraRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Test
    @DisplayName("Deve retornar somente a revendedora que faz aniversário no dia e mês informados")
    void deveRetornarSomenteARevendedoraQueFazAniversarioNoDiaEMesInformados() {
        Revendedora aniversariante = revendedora("Aniversariante", LocalDate.of(1990, 9, 15));
        revendedora("Outro dia", LocalDate.of(1990, 9, 16));
        revendedora("Outro mes", LocalDate.of(1990, 10, 15));
        revendedora("Sem data", null);

        List<Revendedora> encontradas = revendedoraRepository.findAniversariantes(15, 9);

        assertEquals(List.of(aniversariante), encontradas);
    }

    @Test
    @DisplayName("Deve retornar todos os aniversariantes do dia, ignorando o ano de nascimento")
    void deveRetornarTodosOsAniversariantesDoDiaIgnorandoOAnoDeNascimento() {
        Revendedora antiga = revendedora("Antiga", LocalDate.of(1970, 9, 15));
        Revendedora recente = revendedora("Recente", LocalDate.of(2005, 9, 15));

        List<Revendedora> encontradas = revendedoraRepository.findAniversariantes(15, 9);

        assertEquals(2, encontradas.size());
        assertTrue(encontradas.contains(antiga));
        assertTrue(encontradas.contains(recente));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando ninguém faz aniversário no dia")
    void deveRetornarListaVaziaQuandoNinguemFazAniversarioNoDia() {
        revendedora("Outra data", LocalDate.of(1990, 9, 20));

        assertTrue(revendedoraRepository.findAniversariantes(15, 9).isEmpty());
    }

    @Test
    @DisplayName("Deve ignorar revendedoras sem data de nascimento")
    void deveIgnorarRevendedorasSemDataDeNascimento() {
        revendedora("Sem data", null);

        assertTrue(revendedoraRepository.findAniversariantes(15, 9).isEmpty());
    }

    @Test
    @DisplayName("Deve preencher data de cadastro ao criar a revendedora")
    void devePreencherDataDeCadastroAoCriarARevendedora() {
        Revendedora revendedora = revendedora("Com auditoria", LocalDate.of(1990, 9, 15));

        assertNotNull(revendedora.getDataCadastro());
        assertEquals(LocalDate.now(), revendedora.getDataCadastro());
    }

    @Test
    @DisplayName("Deve preencher data de atualização ao alterar a revendedora")
    void devePreencherDataDeAtualizacaoAoAlterarARevendedora() {
        Revendedora revendedora = revendedora("Com auditoria", LocalDate.of(1990, 9, 15));
        revendedora.setNome("Nome alterado");

        Revendedora atualizada = revendedoraRepository.saveAndFlush(revendedora);

        assertNotNull(atualizada.getDataAtualizacao());
        assertEquals(LocalDate.now(), atualizada.getDataAtualizacao());
    }

    @Test
    @DisplayName("Não deve alterar a data de cadastro quando a revendedora for atualizada")
    void naoDeveAlterarDataDeCadastroQuandoARevendedoraForAtualizada() {
        Revendedora revendedora = revendedora("Com auditoria", LocalDate.of(1990, 9, 15));
        LocalDate dataCadastroOriginal = revendedora.getDataCadastro();

        revendedora.setNome("Nome alterado");
        Revendedora atualizada = revendedoraRepository.saveAndFlush(revendedora);

        assertEquals(dataCadastroOriginal, atualizada.getDataCadastro());
    }

    private Revendedora revendedora(String nome, LocalDate dataNascimento) {
        Revendedora revendedora = new Revendedora();
        revendedora.setNome(nome);
        revendedora.setEmail(nome.toLowerCase().replace(' ', '.') + "@email.com");
        revendedora.setTelefone("11987654321");
        revendedora.setEndereco(enderecoRepository.save(endereco(nome)));
        revendedora.setDataNascimento(dataNascimento);
        return revendedoraRepository.save(revendedora);
    }

    private Endereco endereco(String nome) {
        return Endereco.builder()
                .cep("01001000")
                .logradouro("Rua " + nome)
                .numero(100)
                .bairro("Bairro")
                .cidade("Sao Paulo")
                .uf("SP")
                .build();
    }
}
