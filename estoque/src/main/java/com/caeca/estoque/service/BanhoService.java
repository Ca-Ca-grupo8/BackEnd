package com.caeca.estoque.service;

import com.caeca.estoque.database.model.Banho;
import com.caeca.estoque.database.repository.BanhoRepository;
import com.caeca.estoque.dto.BanhoMapper;
import com.caeca.estoque.dto.BanhoRequestDto;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BanhoService {

    private final BanhoRepository banhoRepository;

    public BanhoService(BanhoRepository banhoRepository) {
        this.banhoRepository = banhoRepository;
    }

    public List<Banho> listar() {
        return banhoRepository.findAll();
    }

    public Banho buscarPorId(Integer id) throws NotFoundException {
        return banhoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Banho não encontrado com id: " + id));
    }

    public Banho cadastrar(BanhoRequestDto request) {
        if (banhoRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe um banho com o nome: " + request.getNome()
            );
        }
        return banhoRepository.save(BanhoMapper.toEntity(request));
    }
}
