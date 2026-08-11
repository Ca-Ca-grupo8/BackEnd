package com.caeca.estoque.service.DashEntradaXSaida;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.repository.entradaXSaida.ComparacaoRepository;
import com.caeca.estoque.dto.DashEntradaXSaida.DashEntradaXSaidaDto;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntradaXSaidaService {
    @Autowired
    private ComparacaoRepository comparacaoRepository;

    public DashEntradaXSaidaDto obterResumoDashboard(Long idJoia, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Integer totalEntradas = comparacaoRepository.somarEntradas(idJoia, dataInicio, dataFim);
        Integer totalSaidas = comparacaoRepository.somarSaidas(idJoia, dataInicio, dataFim);
        Integer produtosEmEstoque = totalEntradas - totalSaidas;

        return new DashEntradaXSaidaDto(totalEntradas, totalSaidas, produtosEmEstoque);
    }

    public List<MotivoMovimentacaoRequestDto> obterDadosGrafico(Integer ano, Long idJoia, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (ano == null && dataInicio == null) {
            ano = LocalDate.now().getYear(); // Padrão: ano atual
        }
        return comparacaoRepository.buscarDadosDoGrafico(ano, idJoia, dataInicio, dataFim);
    }
}
