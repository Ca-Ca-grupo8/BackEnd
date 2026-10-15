package com.caeca.estoque.controller;

import com.caeca.estoque.dto.dashboard.FiltroDashboardDto;
import com.caeca.estoque.service.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardComparacaoController {

    private final DashboardService dashboardService;

    public DashboardComparacaoController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping("/kpis-e-grafico")
    public ResponseEntity<Map<String, Object>> obterKpisEGrafico(@ModelAttribute FiltroDashboardDto filtro) {
        Map<String, Object> resultado = dashboardService.buscarKpisEgraficoGeral(filtro);
        return ResponseEntity.ok(resultado);
    }
}
