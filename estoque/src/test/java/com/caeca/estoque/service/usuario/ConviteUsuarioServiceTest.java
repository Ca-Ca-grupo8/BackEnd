package com.caeca.estoque.service.usuario;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.usuario.ConviteUsuario;
import com.caeca.estoque.database.model.usuario.StatusUsuario;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.database.repository.usuario.ConviteUsuarioRepository;
import com.caeca.estoque.database.repository.usuario.UsuarioRepository;
import com.caeca.estoque.dto.usuario.ConviteUsuarioResponseDto;
import com.caeca.estoque.exception.BadRequestException;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConviteUsuarioService")
class ConviteUsuarioServiceTest {

    private static final long EXPIRACAO_HORAS = 24L;
    private static final String FRONTEND_URL = "http://localhost:5173";

    @Mock
    private ConviteUsuarioRepository conviteUsuarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioNotificacaoService usuarioNotificacaoService;

    @Mock
    private NotificacaoEnvioService notificacaoEnvioService;

    private ConviteUsuarioService conviteUsuarioService;

    @BeforeEach
    void setup() {
        conviteUsuarioService = new ConviteUsuarioService(
                conviteUsuarioRepository,
                usuarioRepository,
                passwordEncoder,
                usuarioNotificacaoService,
                notificacaoEnvioService,
                EXPIRACAO_HORAS,
                FRONTEND_URL
        );
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        return usuario;
    }

    @Nested
    @DisplayName("criarConvite()")
    class CriarConviteTests {

        @Test
        @DisplayName("deve salvar usuário PENDENTE e convite com hash do token, retornando o token puro")
        void deveCriarConviteERetornarTokenPuro() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(null);
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            String token = conviteUsuarioService.criarConvite(usuario);

            assertNotNull(token);
            assertDoesNotThrow(() -> UUID.fromString(token));

            assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());

            ArgumentCaptor<ConviteUsuario> captor = ArgumentCaptor.forClass(ConviteUsuario.class);
            verify(conviteUsuarioRepository).save(captor.capture());

            ConviteUsuario convite = captor.getValue();
            assertEquals(usuario, convite.getUsuario());
            assertEquals(HasherToken.gerarHash(token), convite.getTokenHash());
            assertEquals(64, convite.getTokenHash().length());
            assertNotEquals(token, convite.getTokenHash());
            assertFalse(convite.isUtilizado());
            assertFalse(convite.isExpirado());
            assertTrue(convite.getExpiresAt().isAfter(LocalDateTime.now().plusHours(EXPIRACAO_HORAS - 1)));
        }

        @Test
        @DisplayName("deve preservar status já definido no usuário")
        void devePreservarStatusDefinido() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(StatusUsuario.ATIVO);
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            conviteUsuarioService.criarConvite(usuario);

            assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
        }
    }

    @Nested
    @DisplayName("convidar()")
    class ConvidarTests {

        @Test
        @DisplayName("deve criar usuário PENDENTE, registrar envio e publicar convite com link correto")
        void deveConvidarComSucesso() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(null);
            when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            ConviteUsuarioResponseDto response = conviteUsuarioService.convidar(usuario);

            assertEquals(1L, response.getId());
            assertEquals("joao@email.com", response.getEmail());
            assertEquals(StatusUsuario.PENDENTE, response.getStatus());

            verify(notificacaoEnvioService).registrarEnvio(
                    eq(TipoReferencia.USUARIO),
                    eq(1L),
                    eq(UsuarioNotificacaoService.TEMPLATE_CONVITE_USUARIO),
                    eq(true)
            );

            ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
            verify(usuarioNotificacaoService).notificacaoConviteUsuario(
                    eq("joao@email.com"), eq("João Silva"), linkCaptor.capture());

            String link = linkCaptor.getValue();
            assertTrue(link.startsWith(FRONTEND_URL + "/primeiro-acesso?token="));
            assertDoesNotThrow(() -> UUID.fromString(link.substring(link.indexOf("token=") + 6)));
        }

        @Test
        @DisplayName("deve lançar ConflictException quando o e-mail já está cadastrado")
        void deveLancarConflictQuandoEmailJaExiste() {
            Usuario usuario = criarUsuario();
            when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(criarUsuario()));

            assertThrows(ConflictException.class, () -> conviteUsuarioService.convidar(usuario));

            verify(usuarioRepository, never()).save(any());
            verify(usuarioNotificacaoService, never()).notificacaoConviteUsuario(any(), any(), any());
            verify(notificacaoEnvioService, never()).registrarEnvio(any(), anyLong(), anyLong(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("reenviarConvite()")
    class ReenviarConviteTests {

        @Test
        @DisplayName("deve invalidar convites pendentes, gerar novo convite e publicar")
        void deveReenviarComSucesso() {
            Usuario usuario = criarUsuario();
            ConviteUsuario pendente = new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash(UUID.randomUUID().toString()),
                    LocalDateTime.now().plusHours(1),
                    false
            );

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(conviteUsuarioRepository.findByUsuarioIdAndUtilizadoFalse(1L)).thenReturn(List.of(pendente));

            conviteUsuarioService.reenviarConvite(1L);

            assertTrue(pendente.isUtilizado());
            verify(conviteUsuarioRepository).saveAll(List.of(pendente));
            verify(conviteUsuarioRepository).save(any(ConviteUsuario.class));
            verify(usuarioNotificacaoService).notificacaoConviteUsuario(eq("joao@email.com"), eq("João Silva"), any());
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando o usuário não existe")
        void deveLancarNotFoundQuandoUsuarioInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> conviteUsuarioService.reenviarConvite(99L));

            verify(conviteUsuarioRepository, never()).save(any());
            verify(usuarioNotificacaoService, never()).notificacaoConviteUsuario(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("reenviarConviteExpirado()")
    class ReenviarConviteExpiradoTests {

        private ConviteUsuario conviteExpirado(Usuario usuario) {
            return new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash("token-expirado"),
                    LocalDateTime.now().minusHours(1),
                    false
            );
        }

        @Test
        @DisplayName("deve invalidar o token antigo, gerar novo convite e publicar quando expirado")
        void deveReenviarQuandoExpirado() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(StatusUsuario.PENDENTE);
            ConviteUsuario expirado = conviteExpirado(usuario);

            when(conviteUsuarioRepository.findByTokenHash(HasherToken.gerarHash("token-expirado")))
                    .thenReturn(Optional.of(expirado));

            conviteUsuarioService.reenviarConviteExpirado("token-expirado");

            assertTrue(expirado.isUtilizado());
            verify(conviteUsuarioRepository).save(expirado);
            verify(conviteUsuarioRepository).save(argThat(c -> c != expirado));
            verify(usuarioNotificacaoService).notificacaoConviteUsuario(eq("joao@email.com"), eq("João Silva"), any());
            verify(notificacaoEnvioService).registrarEnvio(
                    eq(TipoReferencia.USUARIO), eq(1L),
                    eq(UsuarioNotificacaoService.TEMPLATE_CONVITE_USUARIO), eq(true));
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando o token não existe")
        void deveLancarNotFoundQuandoTokenInexistente() {
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> conviteUsuarioService.reenviarConviteExpirado("token"));

            verify(conviteUsuarioRepository, never()).save(any());
            verify(usuarioNotificacaoService, never()).notificacaoConviteUsuario(any(), any(), any());
        }

        @Test
        @DisplayName("deve lançar BadRequestException quando o convite já foi utilizado")
        void deveLancarBadRequestQuandoUtilizado() {
            Usuario usuario = criarUsuario();
            ConviteUsuario utilizado = new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash("token-expirado"),
                    LocalDateTime.now().minusHours(1),
                    true
            );
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.of(utilizado));

            assertThrows(BadRequestException.class,
                    () -> conviteUsuarioService.reenviarConviteExpirado("token-expirado"));

            verify(conviteUsuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BadRequestException quando o convite ainda é válido")
        void deveLancarBadRequestQuandoAindaValido() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(StatusUsuario.PENDENTE);
            ConviteUsuario valido = new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash("token-valido"),
                    LocalDateTime.now().plusHours(1),
                    false
            );
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.of(valido));

            assertThrows(BadRequestException.class,
                    () -> conviteUsuarioService.reenviarConviteExpirado("token-valido"));

            verify(conviteUsuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BadRequestException quando o usuário já está ativo")
        void deveLancarBadRequestQuandoUsuarioAtivo() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(StatusUsuario.ATIVO);
            ConviteUsuario expirado = conviteExpirado(usuario);
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.of(expirado));

            assertThrows(BadRequestException.class,
                    () -> conviteUsuarioService.reenviarConviteExpirado("token-expirado"));

            verify(conviteUsuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("envio apos commit")
    class EnvioAposCommitTests {

        @Test
        @DisplayName("deve adiar o envio do convite para depois do commit da transacao")
        void deveAdiarEnvioParaDepoisDoCommit() {
            Usuario usuario = criarUsuario();
            usuario.setStatus(null);
            when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            TransactionSynchronizationManager.initSynchronization();
            try {
                conviteUsuarioService.convidar(usuario);

                verify(usuarioNotificacaoService, never())
                        .notificacaoConviteUsuario(any(), any(), any());
                verify(notificacaoEnvioService, never())
                        .registrarEnvio(any(), anyLong(), anyLong(), anyBoolean());

                TransactionSynchronizationManager.getSynchronizations()
                        .forEach(TransactionSynchronization::afterCommit);

                verify(usuarioNotificacaoService)
                        .notificacaoConviteUsuario(eq("joao@email.com"), eq("João Silva"), any());
                verify(notificacaoEnvioService)
                        .registrarEnvio(eq(TipoReferencia.USUARIO), eq(1L),
                                eq(UsuarioNotificacaoService.TEMPLATE_CONVITE_USUARIO), eq(true));
            } finally {
                TransactionSynchronizationManager.clearSynchronization();
            }
        }
    }

    @Nested
    @DisplayName("validarToken()")
    class ValidarTokenTests {

        @Test
        @DisplayName("deve retornar o convite quando o token é válido")
        void deveRetornarConviteValido() {
            String token = UUID.randomUUID().toString();
            ConviteUsuario convite = new ConviteUsuario(
                    criarUsuario(),
                    HasherToken.gerarHash(token),
                    LocalDateTime.now().plusHours(1),
                    false
            );

            when(conviteUsuarioRepository.findByTokenHash(HasherToken.gerarHash(token)))
                    .thenReturn(Optional.of(convite));

            ConviteUsuario resultado = conviteUsuarioService.validarToken(token);

            assertSame(convite, resultado);
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando o token não existe")
        void deveLancarNotFoundQuandoTokenInexistente() {
            String token = UUID.randomUUID().toString();
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> conviteUsuarioService.validarToken(token));
        }

        @Test
        @DisplayName("deve lançar BadRequestException quando o convite já foi utilizado")
        void deveLancarBadRequestQuandoUtilizado() {
            String token = UUID.randomUUID().toString();
            ConviteUsuario convite = new ConviteUsuario(
                    criarUsuario(),
                    HasherToken.gerarHash(token),
                    LocalDateTime.now().plusHours(1),
                    true
            );
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.of(convite));

            assertThrows(BadRequestException.class, () -> conviteUsuarioService.validarToken(token));
        }

        @Test
        @DisplayName("deve lançar BadRequestException quando o convite está expirado")
        void deveLancarBadRequestQuandoExpirado() {
            String token = UUID.randomUUID().toString();
            ConviteUsuario convite = new ConviteUsuario(
                    criarUsuario(),
                    HasherToken.gerarHash(token),
                    LocalDateTime.now().minusHours(1),
                    false
            );
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.of(convite));

            assertThrows(BadRequestException.class, () -> conviteUsuarioService.validarToken(token));
        }
    }

    @Nested
    @DisplayName("definirSenha()")
    class DefinirSenhaTests {

        @Test
        @DisplayName("deve criptografar a senha, ativar o usuário e marcar o convite como utilizado")
        void deveDefinirSenhaComSucesso() {
            String token = UUID.randomUUID().toString();
            Usuario usuario = criarUsuario();
            usuario.setStatus(StatusUsuario.PENDENTE);
            ConviteUsuario convite = new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash(token),
                    LocalDateTime.now().plusHours(1),
                    false
            );

            when(conviteUsuarioRepository.findByTokenHash(HasherToken.gerarHash(token)))
                    .thenReturn(Optional.of(convite));
            when(passwordEncoder.encode("novaSenha")).thenReturn("hashDaNovaSenha");

            conviteUsuarioService.definirSenha(token, "novaSenha");

            assertEquals("hashDaNovaSenha", usuario.getSenha());
            assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
            assertTrue(convite.isUtilizado());

            verify(usuarioRepository).save(usuario);
            verify(conviteUsuarioRepository).save(convite);
        }

        @Test
        @DisplayName("deve lançar BadRequestException e não consumir o convite quando as senhas não conferem")
        void deveLancarBadRequestQuandoSenhasDiferentes() {
            String token = UUID.randomUUID().toString();

            assertThrows(BadRequestException.class,
                    () -> conviteUsuarioService.definirSenha(token, "senhaA", "senhaB"));

            verify(conviteUsuarioRepository, never()).findByTokenHash(any());
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("não deve ativar usuário nem consumir convite quando o token é inválido")
        void naoDeveConsumirConviteQuandoTokenInvalido() {
            String token = UUID.randomUUID().toString();
            when(conviteUsuarioRepository.findByTokenHash(any())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> conviteUsuarioService.definirSenha(token, "novaSenha"));

            verify(usuarioRepository, never()).save(any());
            verify(conviteUsuarioRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("não deve consumir convite quando a criptografia da senha falha")
        void naoDeveConsumirConviteQuandoEncoderFalha() {
            String token = UUID.randomUUID().toString();
            Usuario usuario = criarUsuario();
            ConviteUsuario convite = new ConviteUsuario(
                    usuario,
                    HasherToken.gerarHash(token),
                    LocalDateTime.now().plusHours(1),
                    false
            );

            when(conviteUsuarioRepository.findByTokenHash(HasherToken.gerarHash(token)))
                    .thenReturn(Optional.of(convite));
            when(passwordEncoder.encode(any())).thenThrow(new RuntimeException("Encoder error"));

            assertThrows(RuntimeException.class, () -> conviteUsuarioService.definirSenha(token, "novaSenha"));

            assertFalse(convite.isUtilizado());
            verify(usuarioRepository, never()).save(any());
            verify(conviteUsuarioRepository, never()).save(any());
        }
    }
}
