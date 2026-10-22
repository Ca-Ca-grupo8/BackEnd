package com.caeca.estoque.controller.movimentacaoRevendedora;

import com.caeca.estoque.dto.movimentacaoRevendedora.JoiaSaidaMaletaDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MaletaResumoDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraStatusPatchDto;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Movimentação por Revendedoras", description = "Gerenciamento das saídas destinadas a revendedoras")
public class MovimentacaoRevendedoraController {
    private MovimentacaoRevendedoraService mrService;

    public MovimentacaoRevendedoraController(MovimentacaoRevendedoraService mrService) {
        this.mrService = mrService;
    }

    @GetMapping
    public ResponseEntity<Page<MovimentacaoRevendedoraResponseDto>> listar(
            @PageableDefault(size = 10, sort = "idMovimentacaoRevendedora") Pageable pageable
    ) {
        Page<MovimentacaoRevendedoraResponseDto> response = mrService.listarTodas(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumo")
    public ResponseEntity<MaletaResumoDto> resumo(){
        return ResponseEntity.ok(mrService.resumoNaoFinalizadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoRevendedoraResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(MovimentacaoRevendedoraMapper.toResponse(mrService.buscarPorId(id)));
    }

    @GetMapping("/revendedora/joias-saida")
    public ResponseEntity<List<JoiaSaidaMaletaDto>> listarJoiasComSaida(@RequestParam Long idRevendedora){
        return ResponseEntity.ok(mrService.listarJoiasComSaidaPorRevendedora(idRevendedora));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovimentacaoRevendedoraResponseDto> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid MovimentacaoRevendedoraStatusPatchDto patch){
        return ResponseEntity.ok(MovimentacaoRevendedoraMapper.toResponse(mrService.atualizarStatus(id, patch)));
    }

    @PostMapping()
    public ResponseEntity<MovimentacaoRevendedoraResponseDto> cadastrar(
            @RequestBody @Valid MovimentacaoRevendedoraRequestDto requestDto){
        return ResponseEntity.created(null).body(
                MovimentacaoRevendedoraMapper.toResponse(
                        mrService.criar(requestDto)
                ));
    }
}
