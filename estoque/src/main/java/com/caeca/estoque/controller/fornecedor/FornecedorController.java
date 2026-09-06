package com.caeca.estoque.controller.fornecedor;

import com.caeca.estoque.dto.fornecedor.*;
import com.caeca.estoque.service.fornecedor.FornecedorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Fornecedores", description = "Gerenciamento de fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }


    @GetMapping
    public ResponseEntity<List<FornecedorResponseDto>> listar(
            @RequestParam(required = false) Boolean is_ativo,
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(fornecedorService.listar(is_ativo, nome));
    }


    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FornecedorCreatedResponseDto> criar(
            @Valid @RequestBody FornecedorRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fornecedorService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FornecedorResponseDto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequestDto dto) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dto));
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorStatusDto dto) {
        fornecedorService.atualizarStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FornecedorResponseDto>> listar(
            @PageableDefault(size = 10, sort = "idFornecedor") Pageable pageable
    ) {
        Page<FornecedorResponseDto> response = fornecedorService.listarTodas(pageable);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}