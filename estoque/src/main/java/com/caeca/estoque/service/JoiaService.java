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

        List<JoiaSaidaDTO> joiasSaidaDTO = JoiaMapper.toResponseList(joias);

        return joiasSaidaDTO;
    }

    public JoiaSaidaDTO listarJoiaPorId(Long id) {

        Joia joia = joiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma jóia foi encontrada com este id"));

        JoiaSaidaDTO joiaSaidaDTO = JoiaMapper.toResponse(joia);

        return joiaSaidaDTO;
    }

    public JoiaSaidaDTO criarJoia(Joia joia, Categoria categoria, Banho banho, Material material) {

        if (categoria.getNome() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Nome da jóia não pode estar vazio!");
        }

        if (joia.getMultiplicador() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Multiplicador de preço da jóia não pode estar vazio!");
        }

        if (joia.getPeso() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Peso da jóia não pode estar vazio!");
        }

        if (joia.getPrecoOriginal() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Preço da jóia não pode estar vazio!");
        }

        if (joia.getQuantidadeMinima() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Quantidade mínima não pode estar vazio!");
        }
        return null;
    }
}
