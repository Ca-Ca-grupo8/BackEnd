package com.caeca.estoque.controller.endereco;

import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;
import com.caeca.estoque.dto.endereco.ViaCepResponseDTO;
import com.caeca.estoque.service.endereco.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponseDTO> consultarCep(@PathVariable String cep) {
        ViaCepResponseDTO resposta = enderecoService.consultarCep(cep);
        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.buscarPorId(id));
    }


    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> criar(@RequestBody EnderecoRequestDTO dto) {
        EnderecoResponseDTO criado = enderecoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody EnderecoRequestDTO dto) {
        return ResponseEntity.ok(enderecoService.atualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}