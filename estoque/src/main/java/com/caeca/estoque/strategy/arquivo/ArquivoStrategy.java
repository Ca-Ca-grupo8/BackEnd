package com.caeca.estoque.strategy.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import org.springframework.web.multipart.MultipartFile;

public interface ArquivoStrategy {
    public Arquivo adicionar(MultipartFile arquivo);
    public Arquivo listar();
    public Arquivo atualizarPorId();
    public void deletar();
}
