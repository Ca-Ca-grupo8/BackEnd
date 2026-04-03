package com.caeca.estoque.controller.revendedora;

import com.caeca.estoque.dto.revendedora.RevendedoraMapper;
import com.caeca.estoque.dto.revendedora.RevendedoraResponse;
import com.caeca.estoque.service.revendedora.RevendedoraService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/revendedoras")
class RevendedoraController {
    private final RevendedoraService revendedoraService;

    public RevendedoraController(RevendedoraService revendedoraService) {
        this.revendedoraService = revendedoraService;
    }
}
