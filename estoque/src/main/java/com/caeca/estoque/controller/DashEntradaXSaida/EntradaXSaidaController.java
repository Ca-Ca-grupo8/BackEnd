package com.caeca.estoque.controller.DashEntradaXSaida;

import com.caeca.estoque.dto.DashEntradaXSaida.DashEntradaXSaidaDto;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
import com.caeca.estoque.service.DashEntradaXSaida.EntradaXSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class EntradaXSaidaController {

    @Autowired
    private EntradaXSaidaService comparacaoService;

    // Endpoint dos Cards (Total Entradas, Saídas e Estoque)
    @GetMapping("/resumo")
    public ResponseEntity<DashEntradaXSaidaDto> obterResumo(
            @RequestParam(required = false) Long idJoia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        DashEntradaXSaidaDto resumo = comparacaoService.obterResumoDashboard(idJoia, dataInicio, dataFim);
        return ResponseEntity.ok(resumo);
    }

    // Endpoint do Gráfico de Barras por Mês
    @GetMapping("/grafico")
    public ResponseEntity<List<MotivoMovimentacaoRequestDto>> obterDadosGrafico(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Long idJoia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        List<MotivoMovimentacaoRequestDto> dadosGrafico = comparacaoService.obterDadosGrafico(ano, idJoia, dataInicio, dataFim);
        return ResponseEntity.ok(dadosGrafico);
    }

}
