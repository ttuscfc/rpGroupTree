package org.example.grouptree.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProcessor {

    private static Map<String, Object> globalJson;

    // Método para processar o arquivo e construir o JSON
    public static void loadJsonFromFile(String filePath) throws IOException {
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

    private static String applyMask(String classification) {
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

    private static String generateMask(int length) {
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

    private static void addToJsonTree(Map<String, Object> treeMap, String classification, String descricao) {
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

    // Método para obter o JSON armazenado
    public static Map<String, Object> getGlobalJson() {
        return globalJson;
    }
}
