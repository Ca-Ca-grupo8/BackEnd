package com.caeca.estoque.controller.statusMaleta;

import com.caeca.estoque.database.model.statusMaleta.StatusMaleta;
import com.caeca.estoque.dto.statusMaleta.StatusMaletaMapper;
import com.caeca.estoque.dto.statusMaleta.StatusMaletaResponseDto;
import com.caeca.estoque.service.statusMaleta.StatusMaletaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/status-maletas")
@SecurityRequirement(name = "Bearer")
@Tag(name = "Status Maleta", description = "Gerenciamento dos status possíveis de uma maleta")
public class StatusMaletaController {

    private final StatusMaletaService statusMaletaService;

    public StatusMaletaController(StatusMaletaService statusMaletaService) {
        this.statusMaletaService = statusMaletaService;
    }

    @GetMapping
    public ResponseEntity<List<StatusMaletaResponseDto>> listar() {
        List<StatusMaleta> status = statusMaletaService.listarTodos();

        return ResponseEntity.ok(StatusMaletaMapper.toResponseList(status));
    }
}
