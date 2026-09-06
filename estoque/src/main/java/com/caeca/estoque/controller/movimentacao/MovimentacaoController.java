package com.caeca.estoque.controller.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacao;
import com.caeca.estoque.database.model.tipoMovimentacao.TipoMovimentacaoEnum;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.service.movimentacao.MovimentacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Movimentação", description = "Gerenciamento de movimentações do sistema (entrada, saída, reentrada, etc)")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoMovimentacaoEnum tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ){
        List<Movimentacao> response = movimentacaoService.listar(nome, tipo, dataInicio, dataFim);

        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(MovimentacaoMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(MovimentacaoMapper.toResponse(movimentacaoService.buscarPorId(id)));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<MovimentacaoResponseDto> cadastrar(
            @RequestPart(value = "movimentacaoRequest") @Valid MovimentacaoRequestDto request,
            @RequestPart(value = "arquivo", required = false)MultipartFile arquivo){
        return ResponseEntity.created(null)
                .body(MovimentacaoMapper.toResponse(movimentacaoService.cadastrar(request, arquivo)));
    }
}
