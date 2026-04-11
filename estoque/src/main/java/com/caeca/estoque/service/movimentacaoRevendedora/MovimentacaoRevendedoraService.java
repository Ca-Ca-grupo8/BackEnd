package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraPatchDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.NotFoundException;

import java.util.List;

public class MovimentacaoRevendedoraService {

    private final MovimentacaoRevendedoraRepository mrRepository;

    public MovimentacaoRevendedoraService(MovimentacaoRevendedoraRepository mrRepository) {
        this.mrRepository = mrRepository;
    }

    public List<MovimentacaoRevendedora> listar() {
        return mrRepository.findAll();
    }

    public MovimentacaoRevendedora buscarPorId(Long id) {
        return mrRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda com o id " + id+ " não encontrada."));
    }

    public MovimentacaoRevendedora buscarOuCriarVendaPorRevendedora(MovimentacaoRevendedoraRequestDto requestDto){
        return mrRepository.findByRevendedoraIdRevendedoraAndIsFinalizadaFalse(requestDto.getIdRevendedora())
                .orElse(mrRepository.save(MovimentacaoRevendedoraMapper.toEntity(requestDto)));
    }

    public MovimentacaoRevendedora buscarVendaAbertaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdRevendedoraAndIsFinalizadaFalse(idRevendedora)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public List<MovimentacaoRevendedora> listarPorIdRevendedora(Long idRevendedora){
        return mrRepository.findByRevendedoraIdRevendedora();
    }

    public MovimentacaoRevendedora atualizarFinalizacao(Long id, MovimentacaoRevendedoraPatchDto patch) {
        MovimentacaoRevendedora response = buscarPorId(id);
        response.setFinalizada(patch.isFinalizada());

        mrRepository.save(response);

        return response;
    }
}
