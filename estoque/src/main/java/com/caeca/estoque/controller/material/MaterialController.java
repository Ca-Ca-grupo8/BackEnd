package com.caeca.estoque.controller.material;

import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.dto.material.MaterialMapper;
import com.caeca.estoque.dto.material.MaterialRequestDto;
import com.caeca.estoque.dto.material.MaterialResponseDto;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoMapper;
import com.caeca.estoque.dto.tipoMovimentacao.TipoMovimentacaoResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.Material.MaterialService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
@RequestMapping("/materiais")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Material", description = "Gerenciamento de material das joias")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponseDto>> listar() {
        List<Material> response = materialService.listar();

        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(MaterialMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDto> ExibirPorId(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.ok(MaterialMapper.toResponse(materialService.listarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<MaterialResponseDto> cadastrar(@Valid @RequestBody MaterialRequestDto request){
        Material material = materialService.cadastrar(request);
        return ResponseEntity.created(null).body(MaterialMapper.toResponse(material));

    }


}
