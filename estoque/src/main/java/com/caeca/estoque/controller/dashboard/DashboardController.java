package com.caeca.estoque.controller.dashboard;

import com.caeca.estoque.dto.dashboard.*;
import com.caeca.estoque.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Dashboard", description = "Dados tratados para uso na dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<ValorSaidaDto> buscarValorSaida(FiltroDashboardDTO filtros){
        return ResponseEntity.ok(dashboardService.calcularValorSaida(filtros));
    }

    @GetMapping("/top-cinco")
    public ResponseEntity<List<TopCincoDto>> buscarTopCinco(FiltroDashboardDTO filtros) {
        return ResponseEntity.ok(dashboardService.buscarTopCincoMaisVendidos(filtros));
    }

    @GetMapping("/giro-estoque")
    public ResponseEntity<FiltroGiroEstoque> calcularGiroEstoque(FiltroDashboardDTO filtros) {
        return ResponseEntity.ok(dashboardService.calcularGiroEstoque(filtros));
    }
    @GetMapping("/graficos/motivos-saida")
    public ResponseEntity<List<GraficoMotivoDto>> buscarGraficoMotivosSaida(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Long categoriaId) {

        List<GraficoMotivoDto> dados = dashboardService.obterGraficoMotivosSaida(inicio, fim, categoriaId);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/graficos/motivos-reentrada")
    public ResponseEntity<List<GraficoMotivoDto>> buscarGraficoMotivosReentrada(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Long categoriaId) {

        List<GraficoMotivoDto> dados = dashboardService.obterGraficoMotivosReentrada(inicio, fim, categoriaId);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/top-cinco-mais-frios")
    public ResponseEntity<List<TopCincoMaisFriasDTO>> buscarTopCincoMaisFrios(FiltroDashboardDTO filtro) {
        return ResponseEntity.ok(
                dashboardService.buscarTopCincoMaisFrios(filtro)
        );
    }

    @GetMapping("/indice-pecas-frias")
    public ResponseEntity<IndicePecasFriasDTO> buscarIndicePecasFrias(
            FiltroDashboardDTO filtro
    ) {
        return ResponseEntity.ok(
                dashboardService.buscarIndicePecasFrias(filtro)
        );
    }
}
