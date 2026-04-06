package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.revendedora.RevendedoraMapper;
import com.caeca.estoque.dto.revendedora.RevendedoraPatchDto;
import com.caeca.estoque.dto.revendedora.RevendedoraRequestDto;
import com.caeca.estoque.dto.revendedora.RevendedoraResponseDto;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RevendedoraService {

    private final RevendedoraRepository revendedoraRepository;
    private final EnderecoService enderecoService;

    public RevendedoraService(RevendedoraRepository revendedoraRepository, EnderecoService enderecoService) {
        this.revendedoraRepository = revendedoraRepository;
        this.enderecoService = enderecoService;
    }

    public List<Revendedora> listar() {
        return revendedoraRepository.findAll();
    }

    public Revendedora buscarPorId(Integer id) {
        return revendedoraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Revendedora com ID " + id + " não encontrada"));
    }

    public Revendedora cadastrar(RevendedoraRequestDto request){
        validarDadosDuplicados(request);

        Endereco endereco = enderecoService.buscarPorId(request.getEnderecoId());

        return revendedoraRepository.save(RevendedoraMapper.toEntity(request, endereco));
    }

    public Revendedora atualizarPorCompleto(RevendedoraRequestDto request, Integer id){
        if(!revendedoraRepository.existsById(id)){
            throw new NotFoundException("Revendedora com ID " + id + " não encontrada");
        }

        validarDadosDuplicados(request);

        Endereco endereco = enderecoService.buscarPorId(request.getEnderecoId());
        Revendedora entity = RevendedoraMapper.toEntity(request, endereco);
        entity.setId(id);
        return revendedoraRepository.save(entity);
    }

    public Revendedora atualizarParcialmente(Integer id, RevendedoraPatchDto dto){
        Revendedora entity = revendedoraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Revendedora com ID " + id + " não encontrada"));

        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        if (dto.getEmail() != null) {
            validarEmailDuplicado(dto.getEmail());
            entity.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null) {
            validarTelefoneDuplicado(dto.getTelefone());
            entity.setTelefone(dto.getTelefone());
        }
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
        if (dto.getEnderecoId() != null) {
            validarEnderecoDuplicado(dto.getEnderecoId());
            entity.setEndereco(enderecoService.buscarPorId(dto.getEnderecoId()));
        }

        return revendedoraRepository.save(entity);
    }

    private void validarDadosDuplicados(RevendedoraRequestDto request) {
        validarEmailDuplicado(request.getEmail());

        validarTelefoneDuplicado(request.getTelefone());

        validarEnderecoDuplicado(request.getEnderecoId());
    }

    private void validarEmailDuplicado(String email){
        if (revendedoraRepository.existsByEmail(email)) {
            throw new ConflictException("Email " + email + " já cadastrado");
        }
    }

    private void validarTelefoneDuplicado(String telefone){
        if (revendedoraRepository.existsByTelefone(telefone)) {
            throw new ConflictException("Telefone " + telefone + " já cadastrado");
        }
    }

    private void validarEnderecoDuplicado(Integer enderecoId){
        if (revendedoraRepository.existsByEnderecoId(enderecoId)) {
            throw new ConflictException("Este endereço já está em uso por outra revendedora");
        }
    }
}
