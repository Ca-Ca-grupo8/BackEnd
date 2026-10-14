package com.caeca.estoque.handler;

import com.caeca.estoque.dto.endereco.ErroResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAccessDenied_deveRetornar403() {
        ResponseEntity<ErroResponseDto> resposta =
                handler.handleAccessDenied(new AccessDeniedException("sem permissão"));

        assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Acesso negado", resposta.getBody().getErro());
    }

    @Test
    void handleAccessDenied_deveRetornar403ParaAuthorizationDeniedException() {
        ResponseEntity<ErroResponseDto> resposta =
                handler.handleAccessDenied(new AuthorizationDeniedException("acesso negado"));

        assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Acesso negado", resposta.getBody().getErro());
    }
}
