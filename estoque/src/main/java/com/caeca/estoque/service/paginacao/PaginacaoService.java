package com.caeca.estoque.service.paginacao;

import com.caeca.estoque.dto.material.MaterialResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.function.Function;


@Service
public class PaginacaoService {

    public <T, R> Page<R> buscarPaginado(
            JpaRepository<T, ?> repository,
            Pageable pageable,
            Function<T, R> mapper
    ) {
        return repository.findAll(pageable).map(mapper);
    }
}
