package com.caeca.estoque.service.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.repository.tipoMovimentacao.TipoMovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoMovimentacaoService {

    private final TipoMovimentacaoRepository repository;

    public TipoMovimentacaoService(TipoMovimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<TipoMovimentacao> listarTodos() {
        return repository.findAll();
    }


}