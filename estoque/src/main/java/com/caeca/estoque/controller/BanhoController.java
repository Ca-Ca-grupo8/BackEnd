package com.caeca.estoque.controller;

import com.caeca.estoque.database.model.Banho;
import com.caeca.estoque.dto.BanhoMapper;
import com.caeca.estoque.dto.BanhoRequestDto;
import com.caeca.estoque.dto.BanhoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.BanhoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banhos")
class BanhoController {

    private final BanhoService banhoService;

    public BanhoController(BanhoService banhoService) {
        this.banhoService = banhoService;
    }

    @GetMapping
    public ResponseEntity<List<BanhoResponseDto>> listar() {
        List<Banho> response = banhoService.listar();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BanhoMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BanhoResponseDto> buscarPorId(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.ok(BanhoMapper.toResponse(banhoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<BanhoResponseDto> cadastrar(@Valid @RequestBody BanhoRequestDto request) {
        Banho banho = banhoService.cadastrar(request);
        return ResponseEntity.created(null).body(BanhoMapper.toResponse(banho));
    }
}
