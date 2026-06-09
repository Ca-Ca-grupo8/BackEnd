package com.caeca.estoque.controller.tipoMovimentacao;

import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoMapper;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoRequestDto;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.tipoMovimentacao.TipoMovimentacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-movimentacao")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Tipo de Movimentação", description = "Gerenciamento dos Tipos de movimentações do sistema (entrada, saída, reentrada, etc)")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TipoMovimentacaoResponseDto> cadastrar(@Valid @RequestBody TipoMovimentacaoRequestDto request) {
        TipoMovimentacao tipo = tipoMovimentacaoService.cadastrar(request);
        return ResponseEntity.created(null).body(TipoMovimentacaoMapper.toResponse(tipo));
    }
}
