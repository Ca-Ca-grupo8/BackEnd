package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.database.repository.movimentacao.MovimentacaoRepository;
import com.caeca.estoque.dto.fornecedor.FornecedorMapper;
import com.caeca.estoque.dto.fornecedor.FornecedorResponseDto;
import com.caeca.estoque.dto.joia.*;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JoiaService {

    private final JoiaRepository joiaRepository;
    private final BanhoService banhoService;
    private final CategoriaService categoriaService;
    private final MaterialService materialService;
    private final MovimentacaoRepository movimentacaoRepository;

    public JoiaService(JoiaRepository joiaRepository, BanhoService banhoService, CategoriaService categoriaService, MaterialService materialService, MovimentacaoRepository movimentacaoRepository) {
        this.joiaRepository = joiaRepository;
        this.banhoService = banhoService;
        this.categoriaService = categoriaService;
        this.materialService = materialService;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<JoiaSaidaDto> listarTodasAsJoias(
            String nome,
            Long categoria,
            Long material,
            Long banho,
            JoiaStatus joiaStatus,
            LocalDate dataInicio,
            LocalDate dataFim) {

        List<Joia> joias = joiaRepository.listarTodosFiltrado(
                nome,
                categoria,
                material,
                banho,
                dataInicio,
                dataFim
        );

        if (joias.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada!");
        }

        List<JoiaSaidaDto> response = JoiaMapper.toResponseList(joias);
        response.forEach(dto -> preencherFornecedores(dto, dto.getId()));

        if (joiaStatus != null) {
            response = response
                    .stream()
                    .filter(joia -> joia.getStatus() == joiaStatus)
                    .toList();
        }


        return response;
    }

    public JoiaSaidaDto listarJoiaPorId(Long id) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        JoiaSaidaDto response = JoiaMapper.toResponse(joia);
        preencherFornecedores(response, joia.getId());

        return response;
    }

    public Joia obterEntityJoiaPorId(Long id){
        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        return joia;
    }

    @Transactional
    public JoiaSaidaDto criarJoia(JoiaEntradaDto dto) {

        Categoria categoria = categoriaService.listarCategoriaPorId(dto.getCategoria());

        Banho banho = banhoService.buscarPorId(dto.getBanho());

        Material material = materialService.listarPorId(dto.getMaterial());

        Joia joia = JoiaMapper.toEntity(dto);

        joia.setCategoria(categoria);
        joia.setBanho(banho);
        joia.setMaterial(material);
        joia.setPreco(0.0);

        joia.setAtivo(true);

        Joia joiaCriada = joiaRepository.save(joia);

        JoiaSaidaDto response = JoiaMapper.toResponse(joiaCriada);
        preencherFornecedores(response, joiaCriada.getId());

        return response;
    }

    @Transactional
    public JoiaSaidaDto atualizarJoiaPorId(Long id, JoiaAtualizacaoDto dto) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        aplicarAtualizacao(joia, dto);

        joia.setDataAtualizacao(LocalDateTime.now());

        Joia joiaSalva = joiaRepository.save(joia);

        JoiaSaidaDto response = JoiaMapper.toResponse(joiaSalva);
        preencherFornecedores(response, joiaSalva.getId());

        return response;
    }

    private void aplicarAtualizacao(Joia joia, JoiaAtualizacaoDto dto) {
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            joia.setNome(dto.getNome());
        }

        if (dto.getQuantidadeMinima() != null && dto.getQuantidadeMinima() >= 0) {
            joia.setQuantidadeMinima(dto.getQuantidadeMinima());
        }

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaService.listarCategoriaPorId(dto.getCategoriaId());
            joia.setCategoria(categoria);
        }

        if (dto.getBanhoId() != null) {
            Banho banho = banhoService.buscarPorId(dto.getBanhoId());
            joia.setBanho(banho);
        }

        if (dto.getMaterialId() != null) {
            Material material = materialService.listarPorId(dto.getMaterialId());
            joia.setMaterial(material);
        }

        if (dto.getPrecoFinal() != null) {
            joia.setPreco(dto.getPrecoFinal());
        }

        if(dto.getIsAtivo() != null){
            joia.setAtivo(dto.getIsAtivo());
        }
    }

    private void preencherFornecedores(JoiaSaidaDto joiaSaidaDTO, Long joiaId) {
        List<FornecedorResponseDto> fornecedores = movimentacaoRepository
                .buscarFornecedoresDaJoia(joiaId)
                .stream()
                .map(FornecedorMapper::toResponseDTO)
                .toList();

        joiaSaidaDTO.setFornecedores(fornecedores);
    }

    public void adicionarEstoque(Long id, Integer quantidade) {
        Joia joia = obterEntityJoiaPorId(id);
        joia.setQuantidade(joia.getQuantidade() + quantidade);
        joiaRepository.save(joia);
    }

    public void diminuirEstoque(Long id, Integer quantidade) {
        Joia joia = obterEntityJoiaPorId(id);
        joia.setQuantidade(joia.getQuantidade() - quantidade);
        joiaRepository.save(joia);
    }
}
