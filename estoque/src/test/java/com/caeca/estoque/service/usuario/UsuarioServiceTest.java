package com.caeca.estoque.service.usuario;

import com.caeca.estoque.config.GerenciadorTokenJwt;
import com.caeca.estoque.database.model.usuario.Usuario;
import com.caeca.estoque.database.repository.usuario.UsuarioRepository;
import com.caeca.estoque.dto.usuario.UsuarioListarDto;
import com.caeca.estoque.dto.usuario.UsuarioTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService")
class UsuarioServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("João Silva");
        u.setEmail("joao@email.com");
        u.setSenha("senha123");
        return u;
    }

    @Nested
    @DisplayName("criar()")
    class CriarTests {

        @Test
        @DisplayName("deve criptografar a senha e salvar o usuário")
        void deveCriptografarSenhaESalvar() {
            // Arrange
            Usuario novoUsuario = criarUsuario();
            String senhaCriptografada = "senhaCriptografada";

            Mockito.when(passwordEncoder.encode("senha123")).thenReturn(senhaCriptografada);

            // Act
            usuarioService.criar(novoUsuario);

            // Assert
            assertEquals(senhaCriptografada, novoUsuario.getSenha(),
                    "A senha deve ter sido substituída pela versão criptografada");

            verify(passwordEncoder, times(1)).encode("senha123");
            verify(usuarioRepository, times(1)).save(novoUsuario);
        }

        @Test
        @DisplayName("não deve salvar o usuário quando o PasswordEncoder lançar exceção")
        void naoDeveSalvarQuandoEncoderFalhar() {
            // Arrange
            Usuario novoUsuario = criarUsuario();
            Mockito.when(passwordEncoder.encode(any())).thenThrow(new RuntimeException("Encoder error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> usuarioService.criar(novoUsuario));
            verify(usuarioRepository, never()).save(any());
        }
    }

    // =========================================================================
    // autenticar()
    // =========================================================================

    @Nested
    @DisplayName("autenticar()")
    class AutenticarTests {

        @Test
        @DisplayName("deve retornar UsuarioTokenDto com token quando credenciais são válidas")
        void deveRetornarTokenDtoQuandoCredenciaisValidas() {
            // Arrange
            Usuario usuarioInput = new Usuario();
            usuarioInput.setEmail("joao@email.com");
            usuarioInput.setSenha("senha123");

            Usuario usuarioBanco = criarUsuario();
            String tokenGerado = "jwt.token.aqui";

            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            Mockito.when(usuarioRepository.findByEmail("joao@email.com"))
                    .thenReturn(Optional.of(usuarioBanco));
            Mockito.when(gerenciadorTokenJwt.generateToken(authentication))
                    .thenReturn(tokenGerado);

            SecurityContextHolder.setContext(securityContext);

            UsuarioTokenDto resultado = usuarioService.autenticar(usuarioInput);

            assertNotNull(resultado);
            assertEquals("joao@email.com", resultado.getEmail());
            assertEquals("João Silva", resultado.getNome());
            assertEquals(tokenGerado, resultado.getToken());
            assertEquals(1L, resultado.getUserId());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(gerenciadorTokenJwt).generateToken(authentication);
            verify(securityContext).setAuthentication(authentication);
        }

        @Test
        @DisplayName("deve lançar ResponseStatusException 404 quando email não está cadastrado")
        void deveLancarExcecaoQuandoEmailNaoCadastrado() {
            Usuario usuarioInput = new Usuario();
            usuarioInput.setEmail("inexistente@email.com");
            usuarioInput.setSenha("senha123");

            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            Mockito.when(usuarioRepository.findByEmail("inexistente@email.com"))
                    .thenReturn(Optional.empty());

            SecurityContextHolder.setContext(securityContext);

            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> usuarioService.autenticar(usuarioInput)
            );

            assertEquals(404, ex.getStatusCode().value());
            verify(gerenciadorTokenJwt, never()).generateToken(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando AuthenticationManager rejeitar as credenciais")
        void deveLancarExcecaoQuandoCredenciaisInvalidas() {
            Usuario usuarioInput = new Usuario();
            usuarioInput.setEmail("joao@email.com");
            usuarioInput.setSenha("senhaErrada");

            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciais inválidas"));

            assertThrows(BadCredentialsException.class, () -> usuarioService.autenticar(usuarioInput));
            verify(usuarioRepository, never()).findByEmail(any());
            verify(gerenciadorTokenJwt, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("listarTodos()")
    class ListarTodosTests {

        @Test
        @DisplayName("deve retornar lista de UsuarioListarDto para cada usuário no banco")
        void deveRetornarListaDeDtos() {
            Usuario u1 = criarUsuario();

            Usuario u2 = new Usuario();
            u2.setId(2L);
            u2.setNome("Maria Souza");
            u2.setEmail("maria@email.com");
            u2.setSenha("hashQualquer");

            Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

            // Act
            List<UsuarioListarDto> resultado = usuarioService.listarTodos();

            // Assert
            assertEquals(2, resultado.size());

            assertEquals(1L, resultado.get(0).getId());
            assertEquals("João Silva", resultado.get(0).getNome());
            assertEquals("joao@email.com", resultado.get(0).getEmail());

            assertEquals(2L, resultado.get(1).getId());
            assertEquals("Maria Souza", resultado.get(1).getNome());
            assertEquals("maria@email.com", resultado.get(1).getEmail());

            verify(usuarioRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não houver usuários cadastrados")
        void deveRetornarListaVaziaQuandoSemUsuarios() {
            // Arrange
            Mockito.when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UsuarioListarDto> resultado = usuarioService.listarTodos();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // =========================================================================
    // getUsuarioLogado()
    // =========================================================================

    @Nested
    @DisplayName("getUsuarioLogado()")
    class GetUsuarioLogadoTests {

        @BeforeEach
        void configurarSecurityContext() {
            // Garante que o SecurityContextHolder usa o mock de contexto
            SecurityContextHolder.setContext(securityContext);
        }

        @Test
        @DisplayName("deve retornar o Usuario correspondente ao email no SecurityContext")
        void deveRetornarUsuarioLogado() {
            // Arrange
            Usuario usuarioBanco = criarUsuario();

            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getName()).thenReturn("joao@email.com");
            Mockito.when(usuarioRepository.findByEmail("joao@email.com"))
                    .thenReturn(Optional.of(usuarioBanco));

            // Act
            Usuario resultado = usuarioService.getUsuarioLogado();

            // Assert
            assertNotNull(resultado);
            assertEquals("joao@email.com", resultado.getEmail());
            assertEquals("João Silva", resultado.getNome());
        }

        @Test
        @DisplayName("deve lançar ResponseStatusException 404 quando usuário logado não existe no banco")
        void deveLancarExcecaoQuandoUsuarioLogadoNaoEncontrado() {
            // Arrange
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getName()).thenReturn("fantasma@email.com");
            Mockito.when(usuarioRepository.findByEmail("fantasma@email.com"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> usuarioService.getUsuarioLogado()
            );

            assertEquals(404, ex.getStatusCode().value());
        }
    }
}