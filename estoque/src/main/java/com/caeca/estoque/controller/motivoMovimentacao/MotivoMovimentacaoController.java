package com.caeca.estoque.controller.motivoMovimentacao;

import com.caeca.estoque.database.model.motivoMovimentacao.MotivoMovimentacao;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoMapper;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoRequestDto;
import com.caeca.estoque.dto.motivoMovimentacao.MotivoMovimentacaoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.motivoMovimentacao.MotivoMovimentacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/motivos-movimentacao")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Motivo da Movimentação", description = "Gerenciamento dos motivos de movimentações do sistema (furto, devolução, etc)")
class MotivoMovimentacaoController {

    private final MotivoMovimentacaoService motivoMovimentacaoService;

    public MotivoMovimentacaoController(MotivoMovimentacaoService motivoMovimentacaoService) {
        this.motivoMovimentacaoService = motivoMovimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MotivoMovimentacaoResponseDto>> listar() {
        List<MotivoMovimentacao> response = motivoMovimentacaoService.listar();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(MotivoMovimentacaoMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotivoMovimentacaoResponseDto> buscarPorId(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.ok(MotivoMovimentacaoMapper.toResponse(motivoMovimentacaoService.buscarPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MotivoMovimentacaoResponseDto> cadastrar(@Valid @RequestBody MotivoMovimentacaoRequestDto request) {
        MotivoMovimentacao tipo = motivoMovimentacaoService.cadastrar(request);
        return ResponseEntity.created(null).body(MotivoMovimentacaoMapper.toResponse(tipo));
    }
}
