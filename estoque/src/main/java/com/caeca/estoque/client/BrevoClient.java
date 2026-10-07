package com.caeca.estoque.client;

import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "brevoClient", url = "https://api.brevo.com/v3")
public interface BrevoClient {

    @PostMapping(value = "/smtp/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    void enviarEmailTransacional(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoEmailRequest request
    );
}
