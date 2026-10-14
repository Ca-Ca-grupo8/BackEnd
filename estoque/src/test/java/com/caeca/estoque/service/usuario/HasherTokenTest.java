package com.caeca.estoque.service.usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HasherToken")
class HasherTokenTest {

    @Test
    @DisplayName("deve gerar o hash SHA-256 em hexadecimal (64 caracteres)")
    void deveGerarHashSha256() {
        String hash = HasherToken.gerarHash("abc");

        assertEquals(64, hash.length());
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash);
    }

    @Test
    @DisplayName("deve ser determinístico para a mesma entrada")
    void deveSerDeterministico() {
        assertEquals(HasherToken.gerarHash("token-123"), HasherToken.gerarHash("token-123"));
    }

    @Test
    @DisplayName("deve gerar hashes diferentes para entradas diferentes")
    void deveGerarHashesDiferentes() {
        assertNotEquals(HasherToken.gerarHash("token-a"), HasherToken.gerarHash("token-b"));
    }

    @Test
    @DisplayName("deve lançar exceção para token nulo")
    void deveLancarExcecaoParaTokenNulo() {
        assertThrows(IllegalArgumentException.class, () -> HasherToken.gerarHash(null));
    }
}
