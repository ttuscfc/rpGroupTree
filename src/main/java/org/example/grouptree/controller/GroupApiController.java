package org.example.grouptree.controller;

import org.example.grouptree.model.FileProcessor;
import org.example.grouptree.model.NumberResponse;
import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
public class GroupApiController {

    private final FileProcessorService fileProcessorService;

    @Autowired
    public GroupApiController(FileProcessorService fileProcessorService) {
        this.fileProcessorService = fileProcessorService;
    }

    // API para retornar o JSON completo ou filtrado por classificação
    @GetMapping
    public Map<String, Object> getGrupos(@RequestParam(value = "classificacao", required = false) String classificacao) {
        if (classificacao == null || classificacao.isEmpty()) {
            return fileProcessorService.getGlobalJson(); // Retorna o JSON completo
        } else {
            // Filtra o JSON com base na classificação
            return fileProcessorService.filterByClassificacao(fileProcessorService.getGlobalJson(), classificacao);
        }
    }
}
