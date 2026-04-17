package com.caeca.estoque.controller.categoria;

import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.dto.categoria.CategoriaEntradaDTO;
import com.caeca.estoque.dto.categoria.CategoriaMapper;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDTO;
import com.caeca.estoque.service.categoria.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaSaidaDTO>> listarTodasAsCategorias() {

        List<CategoriaSaidaDTO> categorias = categoriaService.listarTodasAsCategorias();

        return ResponseEntity.status(200).body(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaSaidaDTO> listarCategoriaPorId(@PathVariable Long id) {

        Categoria categoria = categoriaService.listarCategoriaPorId(id);

        CategoriaSaidaDTO categoriaSaidaDTO = CategoriaMapper.toResponse(categoria);

        return ResponseEntity.status(200).body(categoriaSaidaDTO);
    }


    @PostMapping
    public ResponseEntity<CategoriaSaidaDTO> criarCategoria(@RequestBody @Valid CategoriaEntradaDTO categoriaEntradaDTO) {

        Categoria categoria = CategoriaMapper.toEntity(categoriaEntradaDTO);

        CategoriaSaidaDTO categoriaSaidaDTO = categoriaService.criarCategoria(categoria);

        return ResponseEntity.status(201).body(categoriaSaidaDTO);
    }
}
