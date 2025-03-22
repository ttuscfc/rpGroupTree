package org.example.grouptree.controller;

import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grouptree.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

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
    public List<TreeNode> getGrupos(@RequestParam(value = "classificacao", required = false) String classificacao) {
        TreeNode globalJson = fileProcessorService.getGlobalJson();
        List<TreeNode> allJson = new ArrayList<>();
        if (globalJson != null) {
            allJson = globalJson.getGrupos();
        }
        if (classificacao == null || classificacao.isEmpty()) {
            return allJson; // Retorna o JSON completo
        } else {
            // Filtra o JSON com base na classificacao
            return fileProcessorService.filterByClassificacao(globalJson, classificacao);
        }
    }
}
