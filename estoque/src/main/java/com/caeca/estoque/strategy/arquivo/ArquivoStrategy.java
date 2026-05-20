package com.caeca.estoque.strategy.arquivo;

import com.caeca.estoque.database.model.arquivo.Arquivo;
import org.springframework.web.multipart.MultipartFile;

public interface ArquivoStrategy {
    public Arquivo adicionar(MultipartFile arquivo);
    Arquivo atualizarPorJoiaId(Long joiaId, MultipartFile novoArquivo);
    public void deletar(Long id);
    public void deletarPorJoiaId(Long joiaId);
}
