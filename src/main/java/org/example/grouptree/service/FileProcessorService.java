package org.example.grouptree.service;

import org.example.grouptree.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class FileProcessorService {

    private TreeNode globalJson;
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorService.class);

    // Metodo para processar o arquivo e construir o JSON
    public void loadJsonFromFile(String filePath) throws IOException {
        TreeNode rootNode = new TreeNode("raiz", "grupos");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // Le a primeira linha (cabeçalho)

            if (header == null || header.isEmpty()) {
                throw new IllegalArgumentException("O arquivo não contém cabeçalho. Caminho: " + filePath);
            }

            String[] columns = header.split("\\|"); // Divide pelo caractere '|'

            for (int i = 0; i < columns.length; i++) {
                columns[i] = columns[i].replace("\"", "").trim();
            }

            int classificationIndex = -1;
            int descriptionIndex = -1;

            // Localiza os índices das colunas
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equalsIgnoreCase("grup_classificacao")) {
                    classificationIndex = i;
                }
                if (columns[i].equalsIgnoreCase("grup_descricao")) {
                    descriptionIndex = i;
                }
            }

            if (classificationIndex == -1 || descriptionIndex == -1) {
                logger.warn("Colunas necessárias não encontradas no arquivo.");
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

                // Tratamento de valores(classificacao e descricao) nulos ou vazios
                if (classification == null || classification.isEmpty() || descricao == null || descricao.isEmpty()) {
                    throw new IllegalArgumentException("Classificação ou descrição vazia na linha: " + line);
                }

                String formattedClassification = applyMask(classification);
                addToJsonTree(rootNode, formattedClassification, descricao);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // Ordena a arvore
        sortTree(rootNode);
        globalJson = rootNode;  // Armazena o JSON na variavel global
    }

    // Metodo para retornar o JSON completo
    public TreeNode getGlobalJson() {
        return globalJson;
    }

    // Metodo para adicionar os dados ao JSON em arvore
    private void addToJsonTree(TreeNode rootNode, String classification, String descricao) {
        String[] parts = classification.split("\\.");
        StringBuilder path = new StringBuilder();

        TreeNode currentNode = rootNode;
        for (String part : parts) {
            path.append(part);
            String pathStr = path.toString();

            TreeNode nextNode = currentNode.getGrupoByClassificacao(pathStr);
            if (nextNode == null) {
                nextNode = new TreeNode(pathStr, "");
                currentNode.addGrupo(nextNode);
            }
            currentNode = nextNode;
            path.append(".");
        }

        currentNode.setClassificacao(classification);
        currentNode.setDescricao(descricao);
    }

    // Metodo recursivo para ordenar os grupos
    private void sortTree(TreeNode rootNode) {
        List<TreeNode> grupos = rootNode.getGrupos();
        if (grupos != null) {
            // Ordena os grupos na raiz
            grupos.sort(Comparator.comparing(TreeNode::getClassificacao));

            // Recursivamente ordena os subgrupos
            for (TreeNode grupo : grupos) {
                sortTree(grupo); // Ordena recursivamente
            }
        }
    }

    // Metodo para aplicar a mascara
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

    // Metodo que gera a mascara dependendo do tamanho da string
    private String generateMask(int length) {
        StringBuilder mask = new StringBuilder();
        int nivel = 1;
        int numPorNivel = 0;

        for (int i = 0; i < length; i++) {
            mask.append("9");
            numPorNivel++;

            if (numPorNivel == nivel && i < (length - 1)) {
                mask.append(".");
                numPorNivel = 0;
                nivel++;
            }
        }

        return mask.toString();
    }

    // Metodo para filtrar por classificacao
    public List<TreeNode> filterByClassificacao(TreeNode json, String classificacao) {
        List<TreeNode> treeNodeList = new ArrayList<>();
        if (json == null) {
            logger.warn("JSON está nulo");
            return treeNodeList;
        }

        TreeNode filterTreeNode = filterGrupoByClassificacao(json, classificacao);
        if (filterTreeNode != null) treeNodeList.add(filterTreeNode);

        return treeNodeList;
    }

    // Metodo recursivo para filtrar os grupos por classificacao
    private TreeNode filterGrupoByClassificacao(TreeNode grupo, String classificacao) {
        if (grupo.getClassificacao().equals(classificacao)) {
            return grupo;
        }

        List<TreeNode> subGrupos = grupo.getGrupos();
        if (subGrupos != null) {
            for (TreeNode subGrupo : subGrupos) {
                TreeNode filtered = filterGrupoByClassificacao(subGrupo, classificacao);
                if (filtered != null) {
                    return filtered;
                }
            }
        }

        return null;
    }
}
