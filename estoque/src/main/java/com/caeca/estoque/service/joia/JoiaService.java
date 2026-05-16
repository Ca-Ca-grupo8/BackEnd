package com.caeca.estoque.service.joia;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.dto.joia.JoiaAtualizacaoDTO;
import com.caeca.estoque.dto.joia.JoiaEntradaDTO;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import com.caeca.estoque.service.Material.MaterialService;
import com.caeca.estoque.service.banho.BanhoService;
import com.caeca.estoque.service.categoria.CategoriaService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JoiaService {

    private final JoiaRepository joiaRepository;
    private final BanhoService banhoService;
    private final CategoriaService categoriaService;
    private final MaterialService materialService;

    public JoiaService(JoiaRepository joiaRepository, BanhoService banhoService, CategoriaService categoriaService, MaterialService materialService) {
        this.joiaRepository = joiaRepository;
        this.banhoService = banhoService;
        this.categoriaService = categoriaService;
        this.materialService = materialService;
    }

    public List<JoiaSaidaDTO> listarTodasAsJoias() {

        List<Joia> joias = joiaRepository.findAll();

        if (joias.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada!");
        }

        return JoiaMapper.toResponseList(joias);
    }

    public JoiaSaidaDTO listarJoiaPorId(Long id) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        return JoiaMapper.toResponse(joia);
    }

    public Joia obterEntityJoiaPorId(Long id){
        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        return joia;
    }

    @Transactional
    public JoiaSaidaDTO criarJoia(JoiaEntradaDTO dto) {

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

        return JoiaMapper.toResponse(joiaCriada);
    }

    @Transactional
    public JoiaSaidaDTO atualizarJoiaPorId(Long id, JoiaAtualizacaoDTO dto) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jóia não encontrada"));

        // Atualizações simples
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            joia.setNome(dto.getNome());
        }

        if (dto.getQuantidadeMinima() != null && dto.getQuantidadeMinima() >= 0) {
            joia.setQuantidadeMinima(dto.getQuantidadeMinima());
        }

        if (dto.getQuantidade() != null && dto.getQuantidade() > 0) {
            joia.setQuantidade(dto.getQuantidade());
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

        joia.setDataAtualizacao(LocalDateTime.now());

        Joia joiaSalva = joiaRepository.save(joia);

        return JoiaMapper.toResponse(joiaSalva);
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
