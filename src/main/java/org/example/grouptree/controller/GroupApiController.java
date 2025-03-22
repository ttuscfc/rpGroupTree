package org.example.grouptree.controller;

import org.example.grouptree.model.ErrorResponse;
import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getGrupos(@RequestParam(value = "classificacao", required = false) String classificacao) {
        try {
            TreeNode globalJson = fileProcessorService.getGlobalJson();
            List<TreeNode> allJson = new ArrayList<>();
            if (globalJson != null) {
                allJson = globalJson.getGrupos();
            }

            if (classificacao == null || classificacao.isEmpty()) {
                // Retorna o JSON completo
                return ResponseEntity.ok(allJson);
            } else {
                // Filtra o JSON com base na classificacao
                return ResponseEntity.ok(fileProcessorService.filterByClassificacao(globalJson, classificacao));
            }
        } catch (Exception ex) {
            // Em caso de erro, retorna uma resposta de erro
            ErrorResponse errorResponse = new ErrorResponse("Erro inesperado: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
