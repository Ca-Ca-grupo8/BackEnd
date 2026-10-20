package com.caeca.estoque.client;

import com.caeca.estoque.dto.brevo.BrevoEmailListResponse;
import com.caeca.estoque.dto.brevo.BrevoEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/smtp/emails")
    BrevoEmailListResponse listarEmailsTransacionais(
            @RequestHeader("api-key") String apiKey,
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "offset", required = false) Long offset,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "templateId", required = false) Long templateId,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    );
}
