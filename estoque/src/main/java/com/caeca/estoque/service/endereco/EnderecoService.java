package com.caeca.estoque.service.endereco;


import com.caeca.estoque.database.model.Endereco;
import com.caeca.estoque.database.repository.endereco.EnderecoRepository;
import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;
import com.caeca.estoque.dto.endereco.ViaCepResponseDTO;
import com.caeca.estoque.exception.EnderecoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ViaCepClient viaCepClient;

    // GET /enderecos/cep/:cep — consulta ViaCEP
    public ViaCepResponseDTO consultarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        return viaCepClient.buscarPorCep(cepLimpo);
    }

    // GET /enderecos
    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarTodos() {
        return enderecoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // GET /enderecos/:id
    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFoundException(id));
        return toResponse(endereco);
    }

    // POST /enderecos
    @Transactional
    public EnderecoResponseDTO criar(EnderecoRequestDTO dto) {
        Endereco endereco = toEntity(dto);
        return toResponse(enderecoRepository.save(endereco));
    }

    // PUT /enderecos/:id
    @Transactional
    public EnderecoResponseDTO atualizar(Long id, EnderecoRequestDTO dto) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFoundException(id));

        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());

        return toResponse(enderecoRepository.save(endereco));
    }

    // DELETE /enderecos/:id
    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new EnderecoNotFoundException(id);
        }
        enderecoRepository.deleteById(id);
    }

    // --- Mappers ---

    private EnderecoResponseDTO toResponse(Endereco e) {
        return EnderecoResponseDTO.builder()
                .id(e.getId())
                .cep(e.getCep())
                .logradouro(e.getLogradouro())
                .numero(e.getNumero())
                .complemento(e.getComplemento())
                .bairro(e.getBairro())
                .cidade(e.getCidade())
                .uf(e.getUf())
                .build();
    }

    private Endereco toEntity(EnderecoRequestDTO dto) {
        return Endereco.builder()
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .build();
    }
}