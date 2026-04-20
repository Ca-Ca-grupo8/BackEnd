package com.caeca.estoque.controller.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.dto.banho.BanhoMapper;
import com.caeca.estoque.dto.banho.BanhoResponseDto;
import com.caeca.estoque.dto.categoria.CategoriaMapper;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDTO;
import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.material.MaterialMapper;
import com.caeca.estoque.dto.material.MaterialResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.JoiaService;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/joias")
public class JoiaController {

    private final JoiaService joiaService;

    private final CategoriaService categoriaService;
    private final BanhoService banhoService;
    private final MaterialService materialService;

    public JoiaController(JoiaService joiaService, CategoriaService categoriaService, BanhoService banhoService, MaterialService materialService) {
        this.joiaService = joiaService;
        this.categoriaService = categoriaService;
        this.banhoService = banhoService;
        this.materialService = materialService;
    }

    @GetMapping
    public ResponseEntity<List<JoiaSaidaDTO>> listarTodasAsJoias() {

        List<JoiaSaidaDTO> joias = joiaService.listarTodasAsJoias();

        return ResponseEntity.status(200).body(joias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JoiaSaidaDTO> listarJoiaPorId(@RequestBody @Valid Long id) {

        JoiaSaidaDTO joiaSaidaDTO = joiaService.listarJoiaPorId(id);

        return ResponseEntity.status(200).body(joiaSaidaDTO);
    }

    @PostMapping
    public ResponseEntity<JoiaSaidaDTO> criarJoia(@RequestBody JoiaEntradaDTO joia) throws NotFoundException {
        JoiaSaidaDTO joiaSaidaDTO = joiaService.criarJoia(joia);

        return ResponseEntity.status(201).body(joiaSaidaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JoiaSaidaDTO> atualizarJoia(@PathVariable Long id, @RequestBody Joia joiaAtualizada, @RequestParam Long categoriaId, @RequestParam Integer banhoId, @RequestParam Integer materialId) throws NotFoundException {

        Categoria categoria = categoriaService.listarCategoriaPorId(categoriaId);
        Banho banho = banhoService.buscarPorId(banhoId);
        Material material = materialService.listarPorId(materialId);

        JoiaSaidaDTO joiaSaidaDTO = joiaService.atualizarJoiaPorId(id, joiaAtualizada, categoria, banho, material);

        return ResponseEntity.status(200).body(joiaSaidaDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JoiaSaidaDTO> atualizarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {

        JoiaSaidaDTO joiaSaidaDTO = joiaService.atualizarStatusPorId(id, ativo);

        return ResponseEntity.status(200).body(joiaSaidaDTO);
    }
}
