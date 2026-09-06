package com.caeca.estoque.service.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.database.repository.movimentacaoRevendedora.MovimentacaoRevendedoraRepository;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraPatchDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.paginacao.PaginacaoService;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class MovimentacaoRevendedoraService {

    private final MovimentacaoRevendedoraRepository mrRepository;
    private final RevendedoraService revendedoraService;
    private final RevendedoraRepository revendedoraRepository;
    private final PaginacaoService paginacaoService;


    public MovimentacaoRevendedoraService(MovimentacaoRevendedoraRepository mrRepository, RevendedoraService revendedoraService, RevendedoraRepository revendedoraRepository, PaginacaoService paginacaoService) {
        this.mrRepository = mrRepository;
        this.revendedoraService = revendedoraService;
        this.revendedoraRepository = revendedoraRepository;
        this.paginacaoService = paginacaoService;
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
                .orElseGet(() -> {
                    MovimentacaoRevendedora movimentacaoRevendedora = MovimentacaoRevendedoraMapper.toEntity(requestDto);
                    movimentacaoRevendedora.setRevendedora(revendedoraService.buscarPorId(requestDto.getIdRevendedora()));
                    return mrRepository.save(movimentacaoRevendedora);
                });
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

    public Page<MovimentacaoRevendedoraResponseDto> listarTodas(Pageable pageable) {
        return paginacaoService.buscarPaginado(
                mrRepository,
                pageable,
                MovimentacaoRevendedoraMapper::toResponse
        );
    }
}
