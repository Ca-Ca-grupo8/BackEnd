package com.caeca.estoque.service.categoria;

import com.caeca.estoque.database.model.categoria.Categoria;
import com.caeca.estoque.database.repository.categoria.CategoriaRepository;
import com.caeca.estoque.dto.categoria.CategoriaMapper;
import com.caeca.estoque.dto.categoria.CategoriaSaidaDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaSaidaDTO> listarTodasAsCategorias() {

        List<Categoria> categorias = categoriaRepository.findAll();

        if (categorias.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Nenhuma categoria encontrada!");
        }

        List<CategoriaSaidaDTO> categoriasSaidaDTO = CategoriaMapper.toResponseList(categorias);

        return categoriasSaidaDTO;
    }

    public CategoriaSaidaDTO criarCategoria(Categoria categoria) {

        if (categoria.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Nome da categoria não pode estar vazio!");
        }

        Categoria categoriaCriada = categoriaRepository.save(categoria);

        CategoriaSaidaDTO categoriaSaidaDTO = CategoriaMapper.toResponse(categoriaCriada);

        return categoriaSaidaDTO;
    }
}
