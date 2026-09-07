package com.caeca.estoque.controller.joia;

import com.caeca.estoque.dto.joia.*;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.joia.JoiaService;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public ResponseEntity<Page<JoiaSaidaDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) Long material,
            @RequestParam(required = false) Long banho,
            @RequestParam(required = false) JoiaStatus joiaStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        Page<JoiaSaidaDto> response = joiaService.listarTodas(
                nome,
                categoria,
                material,
                banho,
                joiaStatus,
                dataInicio,
                dataFim,
                pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JoiaSaidaDto> listarJoiaPorId(@PathVariable Long id) {

        JoiaSaidaDto joiaSaidaDTO = joiaService.listarJoiaPorId(id);

        return ResponseEntity.status(200).body(joiaSaidaDTO);
    }

    @PostMapping
    public ResponseEntity<JoiaSaidaDto> criarJoia(@RequestBody @Valid JoiaEntradaDto joia) throws NotFoundException {
        JoiaSaidaDto joiaSaidaDTO = joiaService.criarJoia(joia);

        return ResponseEntity.status(201).body(joiaSaidaDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JoiaSaidaDto> atualizarJoiaPorId(@PathVariable Long id, @RequestBody @Valid JoiaAtualizacaoDto dto) {

        JoiaSaidaDto response = joiaService.atualizarJoiaPorId(id, dto);

        return ResponseEntity.status(200).body(response);
    }
}
