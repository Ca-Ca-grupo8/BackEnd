package com.caeca.estoque.controller.arquivo;

import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import com.caeca.estoque.dto.arquivo.ArquivoResponseDto;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy;
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

    @DeleteMapping("/joia/{joiaId}")
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
        return ResponseEntity.ok(
                ArquivoMapper.toResponse(arquivoService.atualizarPorJoiaId(joiaId, arquivo)));
    }
}