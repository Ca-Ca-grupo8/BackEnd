package com.caeca.estoque.controller.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.dto.revendedora.RevendedoraMapper;
import com.caeca.estoque.dto.revendedora.RevendedoraPatchDto;
import com.caeca.estoque.dto.revendedora.RevendedoraRequestDto;
import com.caeca.estoque.dto.revendedora.RevendedoraResponseDto;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/revendedoras")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Revendedoras", description = "Gerenciamento de revendedoras do sistema")
class RevendedoraController {
    private final RevendedoraService revendedoraService;

    public RevendedoraController(RevendedoraService revendedoraService) {
        this.revendedoraService = revendedoraService;
    }

    @GetMapping
    public ResponseEntity<List<RevendedoraResponseDto>> listar(){
        List<Revendedora> response = revendedoraService.listar();
        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(RevendedoraMapper.toResponseList(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevendedoraResponseDto> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(RevendedoraMapper.toResponse(revendedoraService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<RevendedoraResponseDto> cadastrar(@Valid @RequestBody RevendedoraRequestDto request){
        Revendedora revendedora = revendedoraService.cadastrar(request);

        return ResponseEntity.created(null).body(RevendedoraMapper.toResponse(revendedora));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevendedoraResponseDto> atualizarPorCompleto(
            @Valid @RequestBody RevendedoraRequestDto request,
            @PathVariable Long id){
        return ResponseEntity.ok(RevendedoraMapper.toResponse(revendedoraService.atualizarPorCompleto(request, id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RevendedoraResponseDto> atualizarParcialmente(
            @RequestBody RevendedoraPatchDto dto,
            @PathVariable Long id
    ){
        return ResponseEntity.ok(RevendedoraMapper.toResponse(revendedoraService.atualizarParcialmente(id, dto)));
    }
}
