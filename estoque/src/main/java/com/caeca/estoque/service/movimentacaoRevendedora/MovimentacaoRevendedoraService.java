package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraPatchDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoRevendedoraService {

    private final MovimentacaoRevendedoraRepository mrRepository;
    private final RevendedoraService revendedoraService;

    public MovimentacaoRevendedoraService(MovimentacaoRevendedoraRepository mrRepository, RevendedoraService revendedoraService) {
        this.mrRepository = mrRepository;
        this.revendedoraService = revendedoraService;
    }

    public List<MovimentacaoRevendedora> listar() {
        return mrRepository.findAll();
    }

    public MovimentacaoRevendedora buscarPorId(Long id) {
        return mrRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda com o id " + id+ " não encontrada."));
    }

    public MovimentacaoRevendedora buscarOuCriarVendaPorRevendedora(MovimentacaoRevendedoraRequestDto requestDto){
        return mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(requestDto.getIdRevendedora())
                .orElse(mrRepository.save(MovimentacaoRevendedoraMapper.toEntity(requestDto)));
    }

    public MovimentacaoRevendedora buscarVendaAbertaPorRevendedora(Long idRevendedora) {
        return mrRepository.findByRevendedoraIdAndIsFinalizadaFalse(idRevendedora)
                .orElseThrow(() -> new NotFoundException("Venda com o id da revendora " + idRevendedora + " não encontrada."));
    }

    public List<MovimentacaoRevendedora> listarPorIdRevendedora(Long idRevendedora){
        return mrRepository.findByRevendedoraId(idRevendedora);
    }

    public MovimentacaoRevendedora atualizarFinalizacao(Long id, MovimentacaoRevendedoraPatchDto patch) {
        MovimentacaoRevendedora response = buscarPorId(id);
        response.setFinalizada(patch.isFinalizada());

        mrRepository.save(response);

        return response;
    }

    public MovimentacaoRevendedora criar(MovimentacaoRevendedoraRequestDto dto){
        MovimentacaoRevendedora entity = MovimentacaoRevendedoraMapper.toEntity(dto);
        entity.setRevendedora(revendedoraService.buscarPorId(dto.getIdRevendedora()));
        return mrRepository.save(entity);
    }
}
