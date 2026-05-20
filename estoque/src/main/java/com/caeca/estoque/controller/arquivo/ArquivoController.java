package com.caeca.estoque.controller.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import com.caeca.estoque.dto.arquivo.ArquivoResponseDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoMapper;
import com.caeca.estoque.dto.movimentacao.MovimentacaoRequestDto;
import com.caeca.estoque.dto.movimentacao.MovimentacaoResponseDto;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy; // <-- Mude para a Interface!
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/arquivos")
public class ArquivoController {

    private final ArquivoStrategy arquivoService;

    public ArquivoController(ArquivoStrategy arquivoService) {
        this.arquivoService = arquivoService;
    }

    @DeleteMapping("/joia/{joiaId}/deletar")
    public ResponseEntity<ArquivoResponseDto> deleteByJoiaId(@PathVariable Long joiaId) {
        arquivoService.deletarPorJoiaId(joiaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArquivoResponseDto> deleteById(@PathVariable Long id) {
        arquivoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/joia/{joiaId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ArquivoResponseDto> atualizar(
            @PathVariable Long joiaId,
            @RequestPart("arquivo")MultipartFile arquivo){
        if(!arquivo.getContentType().equals())
        return ResponseEntity.ok(
                ArquivoMapper.toResponse(arquivoService.atualizarPorJoiaId(joiaId, arquivo)));
    }
}