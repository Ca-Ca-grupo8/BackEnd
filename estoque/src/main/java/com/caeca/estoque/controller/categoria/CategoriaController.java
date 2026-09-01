package com.caeca.estoque.controller.categoria;

import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.dto.categoria.CategoriaEntradaDto;
import com.caeca.estoque.dto.categoria.CategoriaMapper;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDto;
import com.caeca.estoque.service.categoria.CategoriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Categoria", description = "Gerenciamento de categoria das joias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaSaidaDto>> listarTodasAsCategorias() {

        List<CategoriaSaidaDto> categorias = categoriaService.listarTodasAsCategorias();

        return ResponseEntity.status(200).body(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaSaidaDto> listarCategoriaPorId(@PathVariable Long id) {

        Categoria categoria = categoriaService.listarCategoriaPorId(id);

        CategoriaSaidaDto categoriaSaidaDTO = CategoriaMapper.toResponse(categoria);

        return ResponseEntity.status(200).body(categoriaSaidaDTO);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoriaSaidaDto> criarCategoria(@RequestBody @Valid CategoriaEntradaDto categoriaEntradaDTO) {

        Categoria categoria = CategoriaMapper.toEntity(categoriaEntradaDTO);

        CategoriaSaidaDto categoriaSaidaDTO = categoriaService.criarCategoria(categoria);

        return ResponseEntity.status(201).body(categoriaSaidaDTO);
    }
}
