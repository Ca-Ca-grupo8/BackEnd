package com.caeca.estoque.controller.movimentacao;

import com.caeca.estoque.database.model.movimentacao.Movimentacao;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.service.movimentacao.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDto>> listar(){
        List<Movimentacao> response = movimentacaoService.listar();

        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(MovimentacaoMapper.toReponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(MovimentacaoMapper.ToResponse(movimentacaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDto> cadastrar(
            @RequestBody @Valid MovimentacaoRequestDto request){
        return ResponseEntity.created(null)
                .body(MovimentacaoMapper.toResponse(movimentacaoService.cadastrar(request)));
    }
}
