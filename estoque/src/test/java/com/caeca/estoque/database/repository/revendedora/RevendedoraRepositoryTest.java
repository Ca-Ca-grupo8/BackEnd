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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
