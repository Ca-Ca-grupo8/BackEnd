package com.caeca.estoque.controller.notificacaoBrevo;

import com.caeca.estoque.dto.brevo.NotificacaoBrevoEnvioDto;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoConsultaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notificacoes/brevo")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Notificações Brevo", description = "Histórico de e-mails transacionais enviados")
public class NotificacaoBrevoController {

    private final NotificacaoBrevoConsultaService consultaService;

    public NotificacaoBrevoController(NotificacaoBrevoConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacaoBrevoEnvioDto>> listarEnvios(
            @RequestParam(required = false) Long limit,
            @RequestParam(required = false) Long offset,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String messageId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        return ResponseEntity.ok(consultaService.listarEnvios(
                limit, offset, sort, email, templateId, messageId, startDate, endDate));
    }
}
