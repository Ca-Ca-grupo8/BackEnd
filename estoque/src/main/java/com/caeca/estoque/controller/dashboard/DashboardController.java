package com.caeca.estoque.controller.dashboard;

import com.caeca.estoque.dto.dashboard.FiltroDashboardDTO;
import com.caeca.estoque.dto.dashboard.FiltroGiroEstoque;
import com.caeca.estoque.dto.dashboard.TopCincoDto;
import com.caeca.estoque.dto.dashboard.ValorSaidaDto;
import com.caeca.estoque.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
