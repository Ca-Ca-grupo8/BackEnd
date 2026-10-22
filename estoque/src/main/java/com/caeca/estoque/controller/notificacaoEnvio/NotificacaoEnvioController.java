package com.caeca.estoque.controller.notificacaoEnvio;

import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.dto.notificacaoEnvio.NotificacaoEnvioDto;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacoes/envios")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Notificações", description = "Histórico de envios registrados pela aplicação")
public class NotificacaoEnvioController {

    private final NotificacaoEnvioService notificacaoEnvioService;

    public NotificacaoEnvioController(NotificacaoEnvioService notificacaoEnvioService) {
        this.notificacaoEnvioService = notificacaoEnvioService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificacaoEnvioDto>> listar(
            @RequestParam(required = false) TipoReferencia tipoReferencia,
            @RequestParam(required = false) Long templateId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(notificacaoEnvioService.listarEnvios(tipoReferencia, templateId, pageable));
    }
}
