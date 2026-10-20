package com.caeca.estoque.service.estoque;

import com.caeca.estoque.database.model.historicoEstoque.HistoricoEstoque;
import com.caeca.estoque.database.model.notificacaoEnvio.TipoReferencia;
import com.caeca.estoque.database.repository.historicoEstoque.HistoricoEstoqueRepository;
import com.caeca.estoque.database.repository.joia.JoiaRepository;
import com.caeca.estoque.service.rotina.RotinaDiaria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HistoricoEstoqueRotina implements RotinaDiaria {

    private final JoiaRepository joiaRepository;
    private final HistoricoEstoqueRepository historicoEstoqueRepository;

    public HistoricoEstoqueRotina(JoiaRepository joiaRepository,
                                  HistoricoEstoqueRepository historicoEstoqueRepository) {
        this.joiaRepository = joiaRepository;
        this.historicoEstoqueRepository = historicoEstoqueRepository;
    }

    @Override
    public TipoReferencia nome() {
        return TipoReferencia.ESTOQUE;
    }

    @Override
    public void executar(LocalDate hoje) {
        long total = joiaRepository.somarTotalEstoque();
        HistoricoEstoque registro = historicoEstoqueRepository.findByData(hoje)
                .orElseGet(() -> new HistoricoEstoque(hoje, total));
        registro.setTotalEstoque(total);
        historicoEstoqueRepository.save(registro);
    }
}
