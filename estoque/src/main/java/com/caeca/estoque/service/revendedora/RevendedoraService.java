package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.revendedora.RevendedoraMapper;
import com.caeca.estoque.dto.revendedora.RevendedoraResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RevendedoraService {

    private final RevendedoraRepository revendedoraRepository;

    public RevendedoraService(RevendedoraRepository revendedoraRepository) {
        this.revendedoraRepository = revendedoraRepository;
    }

    public List<RevendedoraResponse> listar() {
        List<Revendedora> response = revendedoraRepository.findAll();
        return RevendedoraMapper.toResponseList(response);
    }

    public Optional<RevendedoraResponse> buscarPorId(Integer id){
        return revendedoraRepository.findById(id)
                .map(RevendedoraMapper::toResponse);
    }
}
