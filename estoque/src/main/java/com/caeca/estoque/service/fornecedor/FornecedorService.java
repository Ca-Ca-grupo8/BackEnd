package com.caeca.estoque.service.fornecedor;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.repository.endereco.EnderecoRepository;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.endereco.EnderecoRequestDTO;
import com.caeca.estoque.dto.endereco.EnderecoResponseDTO;
import com.caeca.estoque.dto.fornecedor.*;
import com.caeca.estoque.exception.FornecedorNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final EnderecoRepository enderecoRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository,
                             EnderecoRepository enderecoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.enderecoRepository = enderecoRepository;
    }


    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listar(Boolean isAtivo, String nome) {
        List<Fornecedor> resultado;

        if (isAtivo != null && nome != null) {
            resultado = fornecedorRepository.findByIsAtivoAndNomeContainingIgnoreCase(isAtivo, nome);
        } else if (isAtivo != null) {
            resultado = fornecedorRepository.findByIsAtivo(isAtivo);
        } else if (nome != null) {
            resultado = fornecedorRepository.findByNomeContainingIgnoreCase(nome);
        } else {
            resultado = fornecedorRepository.findAll();
        }

        return resultado.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));
        return toResponseDTO(fornecedor);
    }


    @Transactional
    public FornecedorCreatedResponseDTO criar(FornecedorRequestDTO dto) {
        Endereco endereco = enderecoRepository.save(toEnderecoEntity(dto.getEndereco()));

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(dto.getNome());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setEndereco(endereco);

        Fornecedor salvo = fornecedorRepository.save(fornecedor);

        return new FornecedorCreatedResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getIsAtivo(),
                salvo.getDataCadastro()
        );
    }


    @Transactional
    public FornecedorResponseDTO atualizar(Long id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));

        Endereco endereco = fornecedor.getEndereco();
        EnderecoRequestDTO endDto = dto.getEndereco();
        endereco.setCep(endDto.getCep());
        endereco.setLogradouro(endDto.getLogradouro());
        endereco.setNumero(endDto.getNumero());
        endereco.setComplemento(endDto.getComplemento());
        endereco.setBairro(endDto.getBairro());
        endereco.setCidade(endDto.getCidade());
        endereco.setUf(endDto.getUf());
        enderecoRepository.save(endereco);

        fornecedor.setNome(dto.getNome());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setTelefone(dto.getTelefone());

        return toResponseDTO(fornecedorRepository.save(fornecedor));
    }


    @Transactional
    public void atualizarStatus(Long id, FornecedorStatusDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));
        fornecedor.setIsAtivo(dto.isAtivo());
        fornecedorRepository.save(fornecedor);
    }



    private FornecedorResponseDTO toResponseDTO(Fornecedor f) {
        EnderecoResponseDTO enderecoDTO = null;
        if (f.getEndereco() != null) {
            Endereco e = f.getEndereco();
            enderecoDTO = new EnderecoResponseDTO(
                    e.getId(), e.getCep(), e.getLogradouro(),
                    e.getNumero(), e.getComplemento(),
                    e.getBairro(), e.getCidade(), e.getUf()
            );
        }

        return new FornecedorResponseDTO(
                f.getId(),
                f.getNome(),
                f.getEmail(),
                f.getTelefone(),
                f.getIsAtivo(),
                f.getDataCadastro(),
                enderecoDTO
        );
    }

    private Endereco toEnderecoEntity(EnderecoRequestDTO dto) {
        Endereco e = new Endereco();
        e.setCep(dto.getCep());
        e.setLogradouro(dto.getLogradouro());
        e.setNumero(dto.getNumero());
        e.setComplemento(dto.getComplemento());
        e.setBairro(dto.getBairro());
        e.setCidade(dto.getCidade());
        e.setUf(dto.getUf());
        return e;
    }
}