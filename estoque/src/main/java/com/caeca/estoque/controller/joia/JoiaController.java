package com.caeca.estoque.controller.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.dto.joia.JoiaAtualizacaoDTO;
import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.dto.joia.StatusDTO;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/joias")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Joias", description = "Gerenciamento das semijoias do sistema")
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
    public ResponseEntity<JoiaSaidaDTO> listarJoiaPorId(@PathVariable Long id) {

        JoiaSaidaDTO joiaSaidaDTO = joiaService.listarJoiaPorId(id);

        return ResponseEntity.status(200).body(joiaSaidaDTO);
    }

    @PostMapping
    public ResponseEntity<JoiaSaidaDTO> criarJoia(@RequestBody JoiaEntradaDTO joia) throws NotFoundException {
        JoiaSaidaDTO joiaSaidaDTO = joiaService.criarJoia(joia);

        return ResponseEntity.status(201).body(joiaSaidaDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JoiaSaidaDTO> atualizarJoiaPorId(@PathVariable Long id, @RequestBody JoiaAtualizacaoDTO dto) {

        JoiaSaidaDTO response = joiaService.atualizarJoiaPorId(id, dto);

        return ResponseEntity.status(200).body(response);
    }
}
