package com.caeca.estoque.service.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.statusMaleta.StatusMaletaRepository;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusMaletaService {

    private final StatusMaletaRepository statusMaletaRepository;

    public StatusMaletaService(StatusMaletaRepository statusMaletaRepository) {
        this.statusMaletaRepository = statusMaletaRepository;
    }

    public List<StatusMaleta> listarTodos() {
        return statusMaletaRepository.findAll();
    }

    public StatusMaleta buscarPorId(Long id) {
        return statusMaletaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Status de maleta não encontrado com o id: " + id));
    }

    public StatusMaleta buscarPorNome(StatusMaletaEnum nome) {
        return statusMaletaRepository.findByNome(nome)
                .orElseThrow(() -> new NotFoundException("Status de maleta não encontrado com o nome: " + nome));
    }
}
