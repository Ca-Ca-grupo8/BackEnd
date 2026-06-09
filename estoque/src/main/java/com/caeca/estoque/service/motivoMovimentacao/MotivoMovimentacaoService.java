package com.caeca.estoque.service.motivoMovimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.motivoMovimentacao.MotivoMovimentacaoRepository;
import com.caeca.estoque.database.repository.tipoMovimentacao.TipoMovimentacaoRepository;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoMapper;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MotivoMovimentacaoService {

    private final MotivoMovimentacaoRepository motivoMovimentacaoRepository;
    private final TipoMovimentacaoRepository tipoMovimentacaoRepository;

    public MotivoMovimentacaoService(MotivoMovimentacaoRepository motivoMovimentacaoRepository, TipoMovimentacaoRepository tipoMovimentacaoRepository) {
        this.motivoMovimentacaoRepository = motivoMovimentacaoRepository;
        this.tipoMovimentacaoRepository = tipoMovimentacaoRepository;
    }

    public List<MotivoMovimentacao> listar() {
        return motivoMovimentacaoRepository.findAll();
    }

    public MotivoMovimentacao buscarPorId(Integer id) throws NotFoundException {
        return motivoMovimentacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Motivo de movimentação não encontrado com id: " + id));
    }

    public MotivoMovimentacao cadastrar(MotivoMovimentacaoRequestDto request) {
        if (motivoMovimentacaoRepository.existsByDescricaoIgnoreCase(request.getDescricao())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe um motivo de movimentação com a descrição: " + request.getDescricao()
            );
        }

        MotivoMovimentacao movimentacao = MotivoMovimentacaoMapper.toEntity(request);

        TipoMovimentacao tipoMovimentacao = tipoMovimentacaoRepository.findById(request.getIdTipoMovimentacao())
                .orElseThrow(() -> new NotFoundException("Tipo movimentação não encontrado"));

        movimentacao.setTipoMovimentacao(tipoMovimentacao);
        return motivoMovimentacaoRepository.save(movimentacao);
    }
}
