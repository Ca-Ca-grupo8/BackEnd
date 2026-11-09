package com.caeca.estoque.client;

import com.caeca.estoque.dto.brevo.BrevoAtualizarContatoRequest;
import com.caeca.estoque.dto.brevo.BrevoContatoRequest;
import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "brevoClient", url = "https://api.brevo.com/v3")
public interface BrevoClient {

    @PostMapping(value = "/smtp/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    void enviarEmailTransacional(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoEmailRequest request
    );

    @PostMapping(value = "/contacts", consumes = MediaType.APPLICATION_JSON_VALUE)
    void criarOuAtualizarContato(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoContatoRequest request
    );

    @PutMapping(value = "/contacts/{identifier}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void atualizarContato(
            @RequestHeader("api-key") String apiKey,
            @PathVariable("identifier") String identifier,
            @RequestParam("identifierType") String identifierType,
            @RequestBody BrevoAtualizarContatoRequest request
    );
}
