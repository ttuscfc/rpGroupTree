package org.example.grouptree.service;

import org.example.grouptree.model.TreeNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileProcessorServiceTest {

    @InjectMocks
    private FileProcessorService fileProcessorService;

    @Test
    public void testLoadJsonFromFile() throws IOException {
        String filePath = "C:/Users/matheus.cabral/Documents/RP Info/teste.txt";
        fileProcessorService.loadJsonFromFile(filePath);

        assertNotNull(fileProcessorService.getGlobalJson());
    }

    @Test
    public void testLoadJsonFromFileColumnError() throws IOException {
        String filePath = "C:/Users/matheus.cabral/Documents/RP Info/testeColunaErro.txt";
        fileProcessorService.loadJsonFromFile(filePath);

        assertNull(fileProcessorService.getGlobalJson());
    }

    @Test
    public void testFilterByClassificacao() {
        TreeNode rootNode = new TreeNode("3", "teste");
        TreeNode rootNode2 = new TreeNode("3.1", "teste2");
        rootNode.addGrupo(rootNode2);

        List<TreeNode> result = fileProcessorService.filterByClassificacao(rootNode, "3");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("3", result.get(0).getClassificacao());
        assertEquals("teste", result.get(0).getDescricao());

        result = fileProcessorService.filterByClassificacao(rootNode, "3.1");
        assertEquals("3.1", result.get(0).getClassificacao());
        assertEquals("teste2", result.get(0).getDescricao());
    }

    @Test
    public void testFilterByClassificacaoReturnNull() {
        TreeNode rootNode = new TreeNode("2", "teste");
        List<TreeNode> treeNodeList = new ArrayList<>();

        List<TreeNode> result = fileProcessorService.filterByClassificacao(rootNode, "3");

        assertEquals(treeNodeList, result);
    }

    @Test
    public void testGetGlobalJson() throws IOException {
        fileProcessorService.loadJsonFromFile("C:/Users/matheus.cabral/Documents/RP Info/teste.txt");

        assertNotNull(fileProcessorService.getGlobalJson());
    }
}
