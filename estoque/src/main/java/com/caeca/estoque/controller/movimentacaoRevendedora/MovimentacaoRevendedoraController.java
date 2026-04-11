package com.caeca.estoque.controller.movimentacaoRevendedora;

import com.caeca.estoque.database.model.movimentacaoRevendedora.MovimentacaoRevendedora;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraMapper;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraPatchDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraRequestDto;
import com.caeca.estoque.dto.movimentacaoRevendedora.MovimentacaoRevendedoraResponseDto;
import com.caeca.estoque.service.movimentacaoRevendedora.MovimentacaoRevendedoraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class MovimentacaoRevendedoraController {
    private MovimentacaoRevendedoraService mrService;

    public MovimentacaoRevendedoraController(MovimentacaoRevendedoraService mrService) {
        this.mrService = mrService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoRevendedoraResponseDto>> listar(){
        List<MovimentacaoRevendedora> response = mrService.listar();

        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(MovimentacaoRevendedoraMapper.toResponseList(response));
    }

    @GetMapping("/id")
    public ResponseEntity<MovimentacaoRevendedoraResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(MovimentacaoRevendedoraMapper.toResponse(mrService.buscarPorId(id)));
    }

    @PatchMapping("/id")
    public ResponseEntity<MovimentacaoRevendedoraResponseDto> alterarFinalizacao(
            @PathVariable Long id,
            @RequestBody MovimentacaoRevendedoraPatchDto patch){
        return ResponseEntity.ok(MovimentacaoRevendedoraMapper.toResponse(mrService.atualizarFinalizacao(id, patch)));
    }
}
