package com.caeca.estoque.controller.banho;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.dto.banho.BanhoMapper;
import com.caeca.estoque.dto.banho.BanhoRequestDto;
import com.caeca.estoque.dto.banho.BanhoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.banho.BanhoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banhos")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Banho", description = "Gerenciamento de Banho das joias")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BanhoResponseDto> cadastrar(@Valid @RequestBody BanhoRequestDto request) {
        Banho banho = banhoService.cadastrar(request);
        return ResponseEntity.created(null).body(BanhoMapper.toResponse(banho));
    }
}
