package com.caeca.estoque.service;

import com.caeca.estoque.database.model.banho.Banho;
import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.model.joia.Joia;
import com.caeca.estoque.database.model.material.Material;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.dto.joia.JoiaMapper;
import com.caeca.estoque.dto.joia.JoiaSaidaDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JoiaService {

    private final JoiaRepository joiaRepository;

    public JoiaService(JoiaRepository joiaRepository) {
        this.joiaRepository = joiaRepository;
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

    public JoiaSaidaDTO criarJoia(Joia joia, Categoria categoria, Banho banho, Material material) {

        if (joia.getNome() == null || joia.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Nome da jóia não pode estar vazio");
        }

        if (joia.getMultiplicador() == null || joia.getMultiplicador() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Multiplicador de preço da jóia deve ser maior que zero");
        }

        if (joia.getPeso() == null || joia.getPeso() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Peso da jóia deve ser maior que zero");
        }

        if (joia.getPrecoOriginal() == null || joia.getPrecoOriginal() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Preço original da jóia deve ser maior que zero");
        }

        if (joia.getQuantidadeMinima() == null || joia.getQuantidadeMinima() < 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Quantidade mínima deve ser maior que zero");
        }

        if (joia.getQuantidade() == null || joia.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Quantidade de jóias deve ser maior que zero");
        }

        if (joia.getPrecoBanho() == null || joia.getPrecoBanho() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Preço do banho deve ser maior que zero");
        }

        joia.setCategoria(categoria);
        joia.setBanho(banho);
        joia.setMaterial(material);

        joia.setDataCadastro(LocalDateTime.now());
        joia.setDataAtualizacao(LocalDateTime.now());

        joia.setAtivo(true);

        Double precoFinal = (joia.getPrecoOriginal() + joia.getPrecoBanho()) * joia.getMultiplicador();

        joia.setPreco(precoFinal);

        Joia joiaCriada = joiaRepository.save(joia);

        return JoiaMapper.toResponse(joiaCriada);
    }

    public JoiaSaidaDTO atualizarJoiaPorId(Long id, Joia joiaAtualizada, Categoria categoria, Banho banho, Material material) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400), "Jóia não encontrada"));

        if (joiaAtualizada.getNome() == null || joiaAtualizada.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Nome da jóia não pode estar vazio");
        }

        if (joiaAtualizada.getMultiplicador() == null || joiaAtualizada.getMultiplicador() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Multiplicador deve ser maior que zero");
        }

        if (joiaAtualizada.getPeso() == null || joiaAtualizada.getPeso() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Peso deve ser maior que zero");
        }

        if (joiaAtualizada.getPrecoOriginal() == null || joiaAtualizada.getPrecoOriginal() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Preço original deve ser maior que zero");
        }

        if (joiaAtualizada.getQuantidadeMinima() == null || joiaAtualizada.getQuantidadeMinima() < 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Quantidade mínima inválida");
        }

        if (joiaAtualizada.getQuantidade() == null || joiaAtualizada.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Quantidade deve ser maior que zero");
        }

        if (joiaAtualizada.getPrecoBanho() == null || joiaAtualizada.getPrecoBanho() <= 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Preço do banho deve ser maior que zero");
        }

        joia.setNome(joiaAtualizada.getNome());
        joia.setMultiplicador(joiaAtualizada.getMultiplicador());
        joia.setPeso(joiaAtualizada.getPeso());
        joia.setPrecoOriginal(joiaAtualizada.getPrecoOriginal());
        joia.setPrecoBanho(joiaAtualizada.getPrecoBanho());
        joia.setQuantidadeMinima(joiaAtualizada.getQuantidadeMinima());
        joia.setQuantidade(joiaAtualizada.getQuantidade());

        joia.setCategoria(categoria);
        joia.setBanho(banho);
        joia.setMaterial(material);

        joia.setDataAtualizacao(LocalDateTime.now());

        Double precoFinal = (joia.getPrecoOriginal() + joia.getPrecoBanho()) * joia.getMultiplicador();
        joia.setPreco(precoFinal);

        Joia joiaSalva = joiaRepository.save(joia);

        return JoiaMapper.toResponse(joiaSalva);
    }

    public JoiaSaidaDTO atualizarStatusPorId (Long id, Boolean ativo) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Joia não encontrada"));

        if (ativo == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Status não pode ser nulo");
        }

        joia.setAtivo(ativo);
        joia.setDataAtualizacao(LocalDateTime.now());

        Joia joiaAtualizada = joiaRepository.save(joia);

        return JoiaMapper.toResponse(joiaAtualizada);
    }
}
