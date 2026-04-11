package com.caeca.estoque.controller;

import com.caeca.estoque.database.model.TipoMovimentacao;
import com.caeca.estoque.dto.TipoMovimentacaoMapper;
import com.caeca.estoque.dto.TipoMovimentacaoRequestDto;
import com.caeca.estoque.dto.TipoMovimentacaoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.TipoMovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-movimentacao")
class TipoMovimentacaoController {

    private final TipoMovimentacaoService tipoMovimentacaoService;

    public TipoMovimentacaoController(TipoMovimentacaoService tipoMovimentacaoService) {
        this.tipoMovimentacaoService = tipoMovimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoMovimentacaoResponseDto>> listar() {
        List<TipoMovimentacao> response = tipoMovimentacaoService.listar();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(TipoMovimentacaoMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoMovimentacaoResponseDto> buscarPorId(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.ok(TipoMovimentacaoMapper.toResponse(tipoMovimentacaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<TipoMovimentacaoResponseDto> cadastrar(@Valid @RequestBody TipoMovimentacaoRequestDto request) {
        TipoMovimentacao tipo = tipoMovimentacaoService.cadastrar(request);
        return ResponseEntity.created(null).body(TipoMovimentacaoMapper.toResponse(tipo));
    }
}
