package com.caeca.estoque.service.Material;

import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.material.MaterialRepository;
import com.caeca.estoque.dto.material.MaterialMapper;
import com.caeca.estoque.dto.material.MaterialRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe um material com o nome: " + request.getNome()
            );
        }
        return materialRepository.save(MaterialMapper.toEntity(request));


    }

}
