package com.caeca.estoque.controller.usuario;

import com.caeca.estoque.dto.usuario.*;
import com.caeca.estoque.service.usuario.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void criar_deveRetornar201() {
        UsuarioCriacaoDto dto = new UsuarioCriacaoDto();
        dto.setNome("João");
        dto.setEmail("joao@email.com");
        dto.setSenha("senha123");

        Mockito.doNothing().when(usuarioService).criar(Mockito.any());

        ResponseEntity<Void> resposta = usuarioController.criar(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
    }

    @Test
    void login_deveRetornar200ComDadosDeSessao() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("joao@email.com");
        loginDto.setSenha("senha123");

        UsuarioTokenDto tokenDto = new UsuarioTokenDto();
        tokenDto.setUserId(1L);
        tokenDto.setNome("João Silva");
        tokenDto.setEmail("joao@email.com");
        tokenDto.setToken("jwt.token.aqui");

        Mockito.when(usuarioService.autenticar(Mockito.any())).thenReturn(tokenDto);

        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<UsuarioSessaoDto> resposta = usuarioController.login(loginDto, response);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("João Silva", resposta.getBody().getNome());
        assertEquals("joao@email.com", resposta.getBody().getEmail());
        assertEquals(1L, resposta.getBody().getUserId());
    }

    @Test
    void login_deveSetarCookieNaResposta() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("joao@email.com");
        loginDto.setSenha("senha123");

        UsuarioTokenDto tokenDto = new UsuarioTokenDto();
        tokenDto.setUserId(1L);
        tokenDto.setNome("João Silva");
        tokenDto.setEmail("joao@email.com");
        tokenDto.setToken("jwt.token.aqui");

        Mockito.when(usuarioService.autenticar(Mockito.any())).thenReturn(tokenDto);

        MockHttpServletResponse response = new MockHttpServletResponse();
        usuarioController.login(loginDto, response);

        String cookie = response.getHeader("Set-Cookie");
        assertTrue(cookie.contains("authToken=jwt.token.aqui"));
        assertTrue(cookie.contains("HttpOnly"));
    }

    @Test
    void logout_deveRetornar204() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Void> resposta = usuarioController.logout(response);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
    }

    @Test
    void logout_deveExpirarCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        usuarioController.logout(response);

        String cookie = response.getHeader("Set-Cookie");
        assertTrue(cookie.contains("authToken="));
        assertTrue(cookie.contains("Max-Age=0"));
    }

    @Test
    void listarTodos_deveRetornar200ComLista() {
        UsuarioListarDto u1 = new UsuarioListarDto();
        u1.setId(1L);
        u1.setNome("João Silva");
        u1.setEmail("joao@email.com");

        UsuarioListarDto u2 = new UsuarioListarDto();
        u2.setId(2L);
        u2.setNome("Maria Souza");
        u2.setEmail("maria@email.com");

        Mockito.when(usuarioService.listarTodos()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<UsuarioListarDto>> resposta = usuarioController.listarTodos();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(2, resposta.getBody().size());
        assertEquals("João Silva", resposta.getBody().get(0).getNome());
        assertEquals("Maria Souza", resposta.getBody().get(1).getNome());
    }

    @Test
    void listarTodos_deveRetornar204QuandoListaVazia() {
        Mockito.when(usuarioService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<UsuarioListarDto>> resposta = usuarioController.listarTodos();

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
    }
}