package org.example.grouptree.service;

import org.example.grouptree.model.GrupoResponse;
import org.example.grouptree.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileProcessorService {

    private GrupoResponse globalJson = new GrupoResponse();
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorService.class);

    // Metodo para processar o arquivo e construir o JSON
    public void loadJsonFromFile(String filePath, String mascara) throws IOException {
        // Valida a máscara
        if (!isValidMask(mascara)) {
            logger.error("Máscara inválida! A máscara deve conter apenas o delimitador '.'");
            return;
        }

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

            // Valida as colunas
            if (classificationIndex == -1 || descriptionIndex == -1) {
                logger.warn("Colunas necessárias não encontradas no arquivo.");
                return;
            }

            String line;
            // Percorre as demais linhas do arquivo para montar a arvore
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

                String formattedClassification = applyMask(classification, mascara);
                if (formattedClassification != null) addToJsonTree(rootNode, formattedClassification, descricao);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // Ordena a arvore
        sortTree(rootNode);
        List<TreeNode> treeNodeList = rootNode.getGrupos();
        // Armazena o JSON na variavel global
        globalJson.setGrupos(treeNodeList);
    }

    // Metodo para validar a máscara
    private boolean isValidMask(String mascara) {
        // Verifica se a máscara contém apenas caracteres alfanuméricos e pontos como delimitadores
        String regex = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mascara);
        return matcher.matches();
    }

    // Metodo para retornar o JSON completo
    public GrupoResponse getGlobalJson() {
        return globalJson;
    }

    // Metodo para adicionar os dados ao JSON em arvore
    private void addToJsonTree(TreeNode rootNode, String classification, String descricao) {
        String[] parts = classification.split("\\.");
        StringBuilder path = new StringBuilder();

        TreeNode currentNode = rootNode;
        for (int i = 0; i < parts.length; i++) {
            path.append(parts[i]);
            String pathStr = path.toString();

            TreeNode nextNode = currentNode.getGrupoByClassificacao(pathStr);
            if (nextNode != null) {
                currentNode = nextNode;
            }

            if (i == parts.length - 1) {
                nextNode = new TreeNode(pathStr, descricao);
                // Verifica se há filhos para o nó
                List<TreeNode> gruposContainsClassificacao = currentNode.getGruposContainsClassificacao(classification + ".");

                if (!gruposContainsClassificacao.isEmpty()) {
                    for (TreeNode grupo : gruposContainsClassificacao) {
                        //adiciona os nós filhos e remove a ligação com o pai anterior
                        nextNode.addGrupo(grupo);
                        currentNode.removeGrupo(grupo);
                    }
                }

                // Adiciona o nó(nextNode) ao pai(currentNode)
                currentNode.addGrupo(nextNode);
            }
            path.append(".");
        }
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
    private String applyMask(String classification, String mask) {
        StringBuilder formatted = new StringBuilder();
        int classIndex = 0;

        // Separa a máscara em partes, considerando os pontos como delimitadores
        String[] maskParts = mask.split("\\.");

        // Verifica se a quantidade de '9' na máscara é suficiente para a classificação
        int maskCount = 0;
        for (String part : maskParts) {
            maskCount += part.length();
        }

        if (maskCount < classification.length()) {
            logger.warn("A máscara " + mask + " não é suficiente para a classificação " + classification + " fornecida.");
            return null;
        }

        // Aplica a máscara
        for (int i = 0; i < maskParts.length; i++) {
            for (int j = 0; j < maskParts[i].length(); j++) {
                if (classIndex < classification.length()) {
                    formatted.append(classification.charAt(classIndex));
                    classIndex++;
                }
            }

            // Adiciona o ponto (se não for a última parte)
            if (classIndex < classification.length() && i < maskParts.length - 1) {
                formatted.append(".");
            }
        }

        return formatted.toString();
    }

    // Metodo para filtrar por classificacao
    public GrupoResponse filterByClassificacao(List<TreeNode> jsonList, String classificacao) {
       List<TreeNode> treeNodeList = new ArrayList<>();
        GrupoResponse grupoResponse = new GrupoResponse(treeNodeList);
        if (jsonList == null) {
            logger.warn("JSON está nulo");
            return grupoResponse;
        }

        for (TreeNode tn : jsonList) {
            TreeNode filterTreeNode = filterGrupoByClassificacao(tn, classificacao);
            if (filterTreeNode != null) {
                treeNodeList.add(filterTreeNode);
                break;
            }
        }

        return grupoResponse;
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
