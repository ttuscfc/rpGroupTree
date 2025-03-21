package org.example.grouptree.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grouptree.model.TreeNode;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessorService {

    private TreeNode globalJson;

    // Método para processar o arquivo e construir o JSON
    public void loadJsonFromFile(String filePath) throws IOException {
        TreeNode root = new TreeNode("grupos", "");  // Nó raiz com o nome "grupos"

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
                addToJsonTree(root, formattedClassification, descricao);
            }
        }

        globalJson = root;  // Armazena a árvore de grupos no objeto globalJson
    }

    // Método para retornar o JSON completo
    public TreeNode getGlobalJson() {
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
    private void addToJsonTree(TreeNode root, String classification, String descricao) {
        String[] parts = classification.split("\\.");
        StringBuilder path = new StringBuilder();

        TreeNode currentNode = root;
        for (String part : parts) {
            path.append(part);
            String pathStr = path.toString();

            TreeNode nextNode = findNode(currentNode, pathStr);
            if (nextNode == null) {
                nextNode = new TreeNode(pathStr, "");
                currentNode.addGrupo(nextNode);
            }

            currentNode = nextNode;
            path.append(".");
        }

        // Adiciona a classificação e descrição ao nó final
        currentNode.setClassificacao(classification);
        currentNode.setDescricao(descricao);
    }

    // Método para encontrar um nó existente na árvore pelo path
    private TreeNode findNode(TreeNode parent, String path) {
        for (TreeNode grupo : parent.getGrupos()) {
            if (grupo.getClassificacao().equals(path)) {
                return grupo;
            }
        }
        return null;
    }

    /*// Método para aplicar a máscara à classificação
    private String applyMask(String classificacao) {
        // Defina a máscara de forma dinâmica: por exemplo, "9.99.999.9999"
        // ou a lógica para dividir em grupos conforme a quantidade de dígitos
        StringBuilder formatted = new StringBuilder();
        int[] mask = {1, 2, 3, 4}; // Exemplo de máscara que define a hierarquia de classificação

        int index = 0;
        for (int i = 0; i < mask.length; i++) {
            int length = mask[i];
            if (index + length <= classificacao.length()) {
                formatted.append(classificacao.substring(index, index + length));
                index += length;
                if (i < mask.length - 1) {
                    formatted.append(".");
                }
            }
        }

        return formatted.toString();
    }*/

    // Método para filtrar por classificação
    public TreeNode filterByClassificacao(TreeNode json, String classificacao) {
        return filterGrupoByClassificacao(json, classificacao);
    }

    // Método recursivo para filtrar os grupos por classificação
    private TreeNode filterGrupoByClassificacao(TreeNode grupo, String classificacao) {
        if (grupo.getClassificacao().equals(classificacao)) {
            return grupo;
        }

        // Verifica os subgrupos
        for (TreeNode subGrupo : grupo.getGrupos()) {
            TreeNode filtered = filterGrupoByClassificacao(subGrupo, classificacao);
            if (filtered != null) {
                return filtered;
            }
        }

        return null;
    }
}
