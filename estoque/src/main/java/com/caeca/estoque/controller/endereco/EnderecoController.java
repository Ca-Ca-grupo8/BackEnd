package com.caeca.estoque.controller.endereco;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.dto.endereco.EnderecoMapper;
import com.caeca.estoque.dto.endereco.EnderecoRequestDto;
import com.caeca.estoque.dto.endereco.EnderecoResponseDto;
import com.caeca.estoque.dto.endereco.ViaCepResponseDto;
import com.caeca.estoque.service.endereco.EnderecoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@Tag(name = "Endereço", description = "Gerenciamento de endereços (CRUD)")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping
    public ResponseEntity<List<EnderecoResponseDto>> listar(){
        return ResponseEntity.ok(enderecoService.listarTodos()
                .stream()
                .map(EnderecoMapper::toResponse)
                .toList());
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponseDto> consultarCep(@PathVariable String cep) {
        ViaCepResponseDto resposta = enderecoService.consultarCep(cep);
        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(EnderecoMapper.toResponse(enderecoService.buscarPorId(id)));
    }


    @PostMapping
    public ResponseEntity<EnderecoResponseDto> criar(@RequestBody EnderecoRequestDto dto) {
        Endereco criado = enderecoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnderecoMapper.toResponse(criado));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EnderecoRequestDto dto) {
        return ResponseEntity.ok(EnderecoMapper.toResponse(enderecoService.atualizar(id, dto)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}