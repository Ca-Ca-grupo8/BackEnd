package com.caeca.estoque.service.endereco;


import com.caeca.estoque.client.ViaCepClient;
import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.repository.endereco.EnderecoRepository;
import com.caeca.estoque.dto.endereco.EnderecoMapper;
import com.caeca.estoque.dto.endereco.EnderecoRequestDto;
import com.caeca.estoque.dto.endereco.EnderecoResponseDto;
import com.caeca.estoque.dto.endereco.ViaCepResponseDto;
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
    public ViaCepResponseDto consultarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        return viaCepClient.buscarPorCep(cepLimpo);
    }

    // GET /enderecos
    @Transactional(readOnly = true)
    public List<Endereco> listarTodos() {
        return enderecoRepository.findAll();
    }

    // GET /enderecos/:id
    @Transactional(readOnly = true)
    public Endereco buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFoundException(id));
        return endereco;
    }

    // POST /enderecos
    @Transactional
    public Endereco criar(EnderecoRequestDto dto) {
        Endereco endereco = EnderecoMapper.toEntity(dto);
        return enderecoRepository.save(endereco);
    }

    // PUT /enderecos/:id
    @Transactional
    public Endereco atualizar(Long id, EnderecoRequestDto dto) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFoundException(id));

        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());

        return enderecoRepository.save(endereco);
    }

    // DELETE /enderecos/:id
    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new EnderecoNotFoundException(id);
        }
        enderecoRepository.deleteById(id);
    }
}