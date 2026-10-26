package com.caeca.estoque.service.revendedora;

import com.caeca.estoque.database.model.endereco.Endereco;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.model.revendedora.Revendedora;
import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import com.caeca.estoque.database.repository.revendedora.RevendedoraRepository;
import com.caeca.estoque.dto.revendedora.RevendedoraMapper;
import com.caeca.estoque.dto.revendedora.RevendedoraPatchDto;
import com.caeca.estoque.dto.revendedora.RevendedoraRequestDto;
import com.caeca.estoque.dto.revendedora.RevendedoraResponseDto;
import com.caeca.estoque.exception.ConflictException;
import com.caeca.estoque.exception.NotFoundException;
import com.caeca.estoque.service.NotificacaoBrevo.NotificacaoBrevoService;
import com.caeca.estoque.service.endereco.EnderecoService;
import com.caeca.estoque.service.notificacaoEnvio.NotificacaoEnvioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class RevendedoraService {

    private final RevendedoraRepository revendedoraRepository;
    private final EnderecoService enderecoService;
    private final NotificacaoBrevoService notificacaoBrevoService;
    private final NotificacaoEnvioService notificacaoEnvioService;


    public RevendedoraService(RevendedoraRepository revendedoraRepository, EnderecoService enderecoService, NotificacaoBrevoService notificacaoBrevoService, NotificacaoEnvioService notificacaoEnvioService) {
        this.revendedoraRepository = revendedoraRepository;
        this.enderecoService = enderecoService;
        this.notificacaoBrevoService = notificacaoBrevoService;
        this.notificacaoEnvioService = notificacaoEnvioService;
    }

    public List<Revendedora> listar() {
        return revendedoraRepository.findAll();
    }

    public Revendedora buscarPorId(Long id) {
        return revendedoraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Revendedora com ID " + id + " não encontrada"));
    }

    public Revendedora cadastrar(RevendedoraRequestDto request){
        validarDadosDuplicados(request, null);

        Endereco endereco = enderecoService.buscarPorId(request.getEnderecoId());

        Revendedora response =revendedoraRepository.save(RevendedoraMapper.toEntity(request, endereco));

        if(notificacaoEnvioService.registrarEnvio(
                TipoReferencia.REVENDEDORA,
                response.getId(),
                NotificacaoBrevoService.TEMPLATE_BOAS_VINDAS,
                false)){
            notificacaoBrevoService.notificacaoBoasVindas(request.getEmail(), request.getNome());
        }

        return response;
    }

    public Revendedora atualizarPorCompleto(RevendedoraRequestDto request, Long id){
        if(!revendedoraRepository.existsById(id)){
            throw new NotFoundException("Revendedora com ID " + id + " não encontrada");
        }

        validarDadosDuplicados(request, id);

        Endereco endereco = enderecoService.buscarPorId(request.getEnderecoId());
        Revendedora entity = RevendedoraMapper.toEntity(request, endereco);
        entity.setId(id);
        return revendedoraRepository.save(entity);
    }

    public Revendedora atualizarParcialmente(Long id, RevendedoraPatchDto dto){
        Revendedora entity = revendedoraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Revendedora com ID " + id + " não encontrada"));

        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        if (dto.getEmail() != null) {
            validarEmailDuplicado(dto.getEmail(), id);
            entity.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null) {
            validarTelefoneDuplicado(dto.getTelefone(), id);
            entity.setTelefone(dto.getTelefone());
        }
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
        if (dto.getEnderecoId() != null) {
            validarEnderecoDuplicado(dto.getEnderecoId(), id);
            entity.setEndereco(enderecoService.buscarPorId(dto.getEnderecoId()));
        }
        if (dto.getDataNascimento() != null) {
            entity.setDataNascimento(dto.getDataNascimento());
        }

        return revendedoraRepository.save(entity);
    }

    private void validarDadosDuplicados(RevendedoraRequestDto request, Long id) {
        validarEmailDuplicado(request.getEmail(), id);

        validarTelefoneDuplicado(request.getTelefone(), id);

        validarEnderecoDuplicado(request.getEnderecoId(), id);
    }

    private void validarEmailDuplicado(String email, Long id) {
        boolean existe = (id != null)
                ? revendedoraRepository.existsByEmailAndIdNot(email, id)
                : revendedoraRepository.existsByEmail(email);

        if (existe) {
            throw new ConflictException("Email " + email + " já cadastrado");
        }
    }

    private void validarTelefoneDuplicado(String telefone, Long id) {
        boolean existe = (id != null)
                ? revendedoraRepository.existsByTelefoneAndIdNot(telefone, id)
                : revendedoraRepository.existsByTelefone(telefone);

        if (existe) {
            throw new ConflictException("Telefone " + telefone + " já cadastrado");
        }
    }

    private void validarEnderecoDuplicado(Long enderecoId, Long id) {
        boolean existe = (id != null)
                ? revendedoraRepository.existsByEnderecoIdAndIdNot(enderecoId, id)
                : revendedoraRepository.existsByEnderecoId(enderecoId);

        if (existe) {
            throw new ConflictException("Este endereço já está em uso por outra revendedora");
        }
    }

    public Page<RevendedoraResponseDto> listarTodas(Pageable pageable) {
        return revendedoraRepository.findAll(pageable)
                .map(RevendedoraMapper::toResponse);
    }

    public Page<RevendedoraResponseDto> listarComMaletaAberta(Pageable pageable) {
        return revendedoraRepository.findComMaletaAberta(
                        List.of(StatusMaletaEnum.FINALIZADA, StatusMaletaEnum.ERRO), pageable)
                .map(RevendedoraMapper::toResponse);
    }
}
