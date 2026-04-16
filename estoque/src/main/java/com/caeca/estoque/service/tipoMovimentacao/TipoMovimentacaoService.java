package com.caeca.estoque.service.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.tipoMovimentacao.TipoMovimentacaoRepository;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoMapper;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TipoMovimentacaoService {

    private final TipoMovimentacaoRepository tipoMovimentacaoRepository;

    public TipoMovimentacaoService(TipoMovimentacaoRepository tipoMovimentacaoRepository) {
        this.tipoMovimentacaoRepository = tipoMovimentacaoRepository;
    }

    public List<TipoMovimentacao> listar() {
        return tipoMovimentacaoRepository.findAll();
    }

    public TipoMovimentacao buscarPorId(Integer id) throws NotFoundException {
        return tipoMovimentacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de movimentação não encontrado com id: " + id));
    }

    public TipoMovimentacao cadastrar(TipoMovimentacaoRequestDto request) {
        if (tipoMovimentacaoRepository.existsByDescricaoIgnoreCase(request.getDescricao())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe um tipo de movimentação com a descrição: " + request.getDescricao()
            );
        }
        return tipoMovimentacaoRepository.save(TipoMovimentacaoMapper.toEntity(request));
    }
}
