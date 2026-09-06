package com.caeca.estoque.service.Material;

import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.material.MaterialRepository;
import com.caeca.estoque.dto.material.MaterialMapper;
import com.caeca.estoque.dto.material.MaterialRequestDto;
import com.caeca.estoque.dto.material.MaterialResponseDto;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.paginacao.PaginacaoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final PaginacaoService paginacaoService;


    public MaterialService(MaterialRepository materialRepository, PaginacaoService paginacaoService) {
        this.materialRepository = materialRepository;
        this.paginacaoService = paginacaoService;
    }

    public List<Material> listar() {
        return materialRepository.findAll();
    }

    public Material listarPorId(Integer id) throws NotFoundException {
        return materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Material não encontrado com id: " + id));
    }

    public Material cadastrar(MaterialRequestDto request){
        if (materialRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException(
                    "Já existe um material com o nome: " + request.getNome()
            );
        }
        return materialRepository.save(MaterialMapper.toEntity(request));
    }

    public Page<MaterialResponseDto> listarTodas(Pageable pageable) {
        return paginacaoService.buscarPaginado(
                materialRepository,
                pageable,
                MaterialMapper::toResponse
        );
    }

}
