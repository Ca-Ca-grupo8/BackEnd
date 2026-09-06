package com.caeca.estoque.service.fornecedor;

import com.caeca.estoque.database.model.fornecedores.Fornecedor;
import com.caeca.estoque.database.repository.fornecedor.FornecedorRepository;
import com.caeca.estoque.dto.fornecedor.*;
import com.caeca.estoque.exception.FornecedorNotFoundException;
import com.caeca.estoque.service.paginacao.PaginacaoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final PaginacaoService paginacaoService;


    public FornecedorService(FornecedorRepository fornecedorRepository, PaginacaoService paginacaoService) {
        this.fornecedorRepository = fornecedorRepository;
        this.paginacaoService = paginacaoService;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDto> listar(Boolean isAtivo, String nome) {
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
    public FornecedorResponseDto buscarPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));
        return toResponseDTO(fornecedor);
    }

    @Transactional
    public FornecedorCreatedResponseDto criar(FornecedorRequestDto dto) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(dto.getNome());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setInstagram(dto.getInstagram());

        Fornecedor salvo = fornecedorRepository.save(fornecedor);

        return new FornecedorCreatedResponseDto(
                salvo.getId(),
                salvo.getNome(),
                salvo.getIsAtivo(),
                salvo.getDataCadastro()
        );
    }

    @Transactional
    public FornecedorResponseDto atualizar(Long id, FornecedorRequestDto dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));

        fornecedor.setNome(dto.getNome());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setInstagram(dto.getInstagram());

        return toResponseDTO(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void atualizarStatus(Long id, FornecedorStatusDto dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException(id));
        fornecedor.setIsAtivo(dto.isAtivo());
        fornecedorRepository.save(fornecedor);
    }

    private FornecedorResponseDto toResponseDTO(Fornecedor f) {
        return new FornecedorResponseDto(
                f.getId(),
                f.getNome(),
                f.getInstagram(),
                f.getTelefone(),
                f.getIsAtivo(),
                f.getDataCadastro()
        );
    }

    public Page<FornecedorResponseDto> listarTodas(Pageable pageable) {
        return paginacaoService.buscarPaginado(
                fornecedorRepository,
                pageable,
                FornecedorMapper::toResponseDTO
        );
    }
}