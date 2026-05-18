package com.caeca.estoque.controller.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import com.caeca.estoque.dto.arquivo.ArquivoMapper;
import com.caeca.estoque.dto.arquivo.ArquivoResponseDto;
import com.caeca.estoque.strategy.arquivo.ArquivoStrategy; // <-- Mude para a Interface!
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoResponseDto> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ArquivoMapper.toResponse(arquivoService.adicionar(file)));
    }
}