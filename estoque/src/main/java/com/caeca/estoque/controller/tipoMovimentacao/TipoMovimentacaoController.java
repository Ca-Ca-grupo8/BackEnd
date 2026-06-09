package com.caeca.estoque.controller.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-movimentacao")
public class TipoMovimentacaoController {

    private final TipoMovimentacaoService service;

    public TipoMovimentacaoController(TipoMovimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoMovimentacao>> listar() {
        List<TipoMovimentacao> tipos = service.listarTodos();
        return ResponseEntity.ok(tipos);
    }
}