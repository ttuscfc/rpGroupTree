package org.example.grouptree.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessorService {

    private Map<String, Object> globalJson;

    // Método para processar o arquivo e construir o JSON
    public void loadJsonFromFile(String filePath) throws IOException {
        Map<String, Object> treeMap = new HashMap<>();
        List<Map<String, Object>> gruposList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // Lê a primeira linha (cabeçalho)
            String[] columns = header.split("\\|"); // Divide pelo caractere '|'

            for (int i = 0; i < columns.length; i++) {
                columns[i] = columns[i].replace("\"", "").trim();
            }

            int classificationIndex = -1;
            int descriptionIndex = -1;

            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equalsIgnoreCase("grup_classificacao")) {
                    classificationIndex = i;
                }
                if (columns[i].equalsIgnoreCase("grup_descricao")) {
                    descriptionIndex = i;
                }
            }

            if (classificationIndex == -1 || descriptionIndex == -1) {
                System.out.println("Colunas necessárias não encontradas no arquivo.");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\|");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replace("\"", "").trim();
                }

                String classification = values[classificationIndex];
                String descricao = values[descriptionIndex];

                String formattedClassification = applyMask(classification);
                addToJsonTree(treeMap, formattedClassification, descricao);
            }
        }

        treeMap.put("grupos", gruposList);
        globalJson = treeMap;  // Armazena o JSON na variável global
    }

    // Método para retornar o JSON completo
    public Map<String, Object> getGlobalJson() {
        return globalJson;
    }

    // Método para aplicar a máscara
    private String applyMask(String classification) {
        String mask = generateMask(classification.length());
        StringBuilder formatted = new StringBuilder();
        int classIndex = 0;

        for (int i = 0; i < mask.length(); i++) {
            if (mask.charAt(i) == '9') {
                formatted.append(classification.charAt(classIndex));
                classIndex++;
            } else {
                formatted.append(mask.charAt(i));
            }
        }
        return formatted.toString();
    }

    private String generateMask(int length) {
        switch (length) {
            case 1:
                return "9";
            case 3:
                return "9.99";
            case 6:
                return "9.99.999";
            case 10:
                return "9.99.999.9999";
            default:
                return "9".repeat(length);
        }
    }

    // Método para adicionar os dados ao JSON em árvore
    private void addToJsonTree(Map<String, Object> treeMap, String classification, String descricao) {
        String[] parts = classification.split("\\.");
        StringBuilder path = new StringBuilder();

        Map<String, Object> currentNode = treeMap;
        for (String part : parts) {
            path.append(part);
            String pathStr = path.toString();

            Map<String, Object> nextNode = (Map<String, Object>) currentNode.get(pathStr);
            if (nextNode == null) {
                nextNode = new HashMap<>();
                currentNode.put(pathStr, nextNode);
            }
            currentNode = nextNode;
            path.append(".");
        }

        currentNode.put("classificacao", classification);
        currentNode.put("descricao", descricao);
    }

    // Método para filtrar por classificação
    public Map<String, Object> filterByClassificacao(Map<String, Object> json, String classificacao) {
        List<Map<String, Object>> filteredGrupos = new ArrayList<>();

        List<Map<String, Object>> grupos = (List<Map<String, Object>>) json.get("grupos");
        for (Map<String, Object> grupo : grupos) {
            Map<String, Object> filtered = filterGrupoByClassificacao(grupo, classificacao);
            if (filtered != null) {
                filteredGrupos.add(filtered);
            }
        }

        Map<String, Object> filteredJson = new HashMap<>();
        filteredJson.put("grupos", filteredGrupos);
        return filteredJson;
    }

    // Método recursivo para filtrar os grupos por classificação
    private Map<String, Object> filterGrupoByClassificacao(Map<String, Object> grupo, String classificacao) {
        if (grupo.get("classificacao").equals(classificacao)) {
            return grupo;
        }

        List<Map<String, Object>> subGrupos = (List<Map<String, Object>>) grupo.get("grupos");
        if (subGrupos != null) {
            for (Map<String, Object> subGrupo : subGrupos) {
                Map<String, Object> filtered = filterGrupoByClassificacao(subGrupo, classificacao);
                if (filtered != null) {
                    return filtered;
                }
            }
        }

        return null;
    }
}
