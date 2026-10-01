package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.revendedora.RevendedoraPatchDto;
import com.caeca.estoque.dto.revendedora.RevendedoraRequestDto;
import com.caeca.estoque.service.endereco.EnderecoService;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RevendedoraServiceTest {

    @Mock
    private RevendedoraRepository revendedoraRepository;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private NotificacaoBrevoService notificacaoBrevoService;

    @InjectMocks
    private RevendedoraService revendedoraService;

    @Test
    @DisplayName("Deve listar todas as revendedoras")
    void deveListarTodasAsRevendedoras() {

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Maria");

        Mockito.when(revendedoraRepository.findAll())
                .thenReturn(List.of(revendedora));

        List<Revendedora> resultado =
                revendedoraService.listar();

        Assertions.assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar revendedora por id")
    void deveBuscarRevendedoraPorId() {

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Joana");

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.of(revendedora));

        Revendedora resultado =
                revendedoraService.buscarPorId(1L);

        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("Joana", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar revendedora inexistente")
    void deveLancarExcecaoAoBuscarRevendedoraInexistente() {

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> revendedoraService.buscarPorId(1L)
        );
    }

    @Test
    @DisplayName("Deve cadastrar revendedora")
    void deveCadastrarRevendedora() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();
        dto.setNome("Ana");
        dto.setEmail("ana@email.com");
        dto.setTelefone("11999999999");
        dto.setEnderecoId(1L);

        Endereco endereco = new Endereco();
        endereco.setId(1L);

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Ana");

        Mockito.when(revendedoraRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByTelefone(dto.getTelefone()))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByEnderecoId(dto.getEnderecoId()))
                .thenReturn(false);

        Mockito.when(enderecoService.buscarPorId(1L))
                .thenReturn(endereco);

        Mockito.when(revendedoraRepository.save(Mockito.any(Revendedora.class)))
                .thenReturn(revendedora);

        Revendedora resultado =
                revendedoraService.cadastrar(dto);

        Assertions.assertEquals("Ana", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar email duplicado")
    void deveLancarExcecaoAoCadastrarEmailDuplicado() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();
        dto.setEmail("teste@email.com");
        dto.setTelefone("11999999999");
        dto.setEnderecoId(1L);

        Mockito.when(revendedoraRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.cadastrar(dto)
        );
    }

    @Test
    @DisplayName("Deve atualizar revendedora")
    void deveAtualizarRevendedora() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();
        dto.setNome("Carlos");
        dto.setEmail("carlos@email.com");
        dto.setTelefone("11888888888");
        dto.setEnderecoId(1L);

        Endereco endereco = new Endereco();
        endereco.setId(1L);

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Carlos");

        Mockito.when(revendedoraRepository.existsById(1L))
                .thenReturn(true);

        Mockito.when(revendedoraRepository.existsByEmailAndIdNot(dto.getEmail(), 1L))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByTelefoneAndIdNot(dto.getTelefone(), 1L))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByEnderecoIdAndIdNot(dto.getEnderecoId(), 1L))
                .thenReturn(false);

        Mockito.when(enderecoService.buscarPorId(1L))
                .thenReturn(endereco);

        Mockito.when(revendedoraRepository.save(Mockito.any(Revendedora.class)))
                .thenReturn(revendedora);

        Revendedora resultado =
                revendedoraService.atualizarPorCompleto(dto, 1L);

        Assertions.assertEquals("Carlos", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar revendedora inexistente")
    void deveLancarExcecaoAoAtualizarRevendedoraInexistente() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();

        Mockito.when(revendedoraRepository.existsById(1L))
                .thenReturn(false);

        Assertions.assertThrows(
                NotFoundException.class,
                () -> revendedoraService.atualizarPorCompleto(dto, 1L)
        );
    }

    @Test
    @DisplayName("Deve atualizar parcialmente revendedora")
    void deveAtualizarParcialmenteRevendedora() {

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);
        revendedora.setNome("Antigo");

        RevendedoraPatchDto dto = new RevendedoraPatchDto();
        dto.setNome("Novo Nome");

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.of(revendedora));

        Mockito.when(revendedoraRepository.save(revendedora))
                .thenReturn(revendedora);

        Revendedora resultado =
                revendedoraService.atualizarParcialmente(1L, dto);

        Assertions.assertEquals("Novo Nome", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar parcialmente revendedora inexistente")
    void deveLancarExcecaoAoAtualizarParcialmenteRevendedoraInexistente() {

        RevendedoraPatchDto dto = new RevendedoraPatchDto();

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> revendedoraService.atualizarParcialmente(1L, dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar email duplicado")
    void deveLancarExcecaoAoAtualizarEmailDuplicado() {

        Revendedora revendedora = new Revendedora();
        revendedora.setId(1L);

        RevendedoraPatchDto dto = new RevendedoraPatchDto();
        dto.setEmail("duplicado@email.com");

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.of(revendedora));

        Mockito.when(revendedoraRepository.existsByEmailAndIdNot(dto.getEmail(), 1L))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.atualizarParcialmente(1L, dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar telefone duplicado")
    void deveLancarExcecaoAoCadastrarTelefoneDuplicado() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();
        dto.setEmail("teste@email.com");
        dto.setTelefone("11999999999");
        dto.setEnderecoId(1L);

        Mockito.when(revendedoraRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByTelefone(dto.getTelefone()))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.cadastrar(dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar endereço duplicado")
    void deveLancarExcecaoAoCadastrarEnderecoDuplicado() {

        RevendedoraRequestDto dto = new RevendedoraRequestDto();
        dto.setEmail("teste@email.com");
        dto.setTelefone("11999999999");
        dto.setEnderecoId(1L);

        Mockito.when(revendedoraRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByTelefone(dto.getTelefone()))
                .thenReturn(false);

        Mockito.when(revendedoraRepository.existsByEnderecoId(dto.getEnderecoId()))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.cadastrar(dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar telefone duplicado")
    void deveLancarExcecaoAoAtualizarTelefoneDuplicado() {

        Revendedora atual = new Revendedora();
        atual.setId(1L);

        RevendedoraPatchDto dto = new RevendedoraPatchDto();
        dto.setTelefone("11999999999");

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.of(atual));

        Mockito.when(revendedoraRepository.existsByTelefoneAndIdNot(dto.getTelefone(), 1L))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.atualizarParcialmente(1L, dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar endereço duplicado")
    void deveLancarExcecaoAoAtualizarEnderecoDuplicado() {

        Revendedora atual = new Revendedora();
        atual.setId(1L);

        RevendedoraPatchDto dto = new RevendedoraPatchDto();
        dto.setEnderecoId(10L);

        Mockito.when(revendedoraRepository.findById(1L))
                .thenReturn(Optional.of(atual));

        Mockito.when(revendedoraRepository.existsByEnderecoIdAndIdNot(10L, 1L))
                .thenReturn(true);

        Assertions.assertThrows(
                ConflictException.class,
                () -> revendedoraService.atualizarParcialmente(1L, dto)
        );
    }
}