package org.example.grouptree.service;

import org.example.grouptree.model.GrupoResponse;
import org.example.grouptree.model.TreeNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileProcessorServiceTest {

    @InjectMocks
    private FileProcessorService fileProcessorService;

    @Test
    public void testLoadJsonFromFile() throws IOException {
        Path path = Paths.get("arquivos/teste.txt");
        String filePath = path.toAbsolutePath().toString();
        String mask = "9.99.999.9999";

        fileProcessorService.loadJsonFromFile(filePath, mask);

        assertNotNull(fileProcessorService.getGlobalJson());
    }

    @Test
    public void testLoadJsonFromFileColumnError() throws IOException {
        Path path = Paths.get("arquivos/testeColunaErro.txt");
        String filePath = path.toAbsolutePath().toString();
        String mask = "9.99.999.9999";

        GrupoResponse grupoResponse = new GrupoResponse();
        fileProcessorService.loadJsonFromFile(filePath, mask);

        GrupoResponse globalJson = fileProcessorService.getGlobalJson();

        assertNotNull(globalJson);
        assertEquals(grupoResponse.getGrupos(), globalJson.getGrupos());
    }

    @Test
    public void testLoadJsonFromFileClassificationEmpty() throws IOException {
        Path path = Paths.get("arquivos/testeSemClassificacao.txt");
        String filePath = path.toAbsolutePath().toString();
        String mask = "9.99.999.9999";

        GrupoResponse grupoResponse = new GrupoResponse(new ArrayList<>());
        fileProcessorService.loadJsonFromFile(filePath, mask);

        GrupoResponse globalJson = fileProcessorService.getGlobalJson();

        assertNotNull(globalJson);
        assertEquals(grupoResponse.getGrupos(), globalJson.getGrupos());
    }

    @Test
    public void testLoadJsonFromFileFileEmpty() throws IOException {
        Path path = Paths.get("arquivos/testeVazio.txt");
        String filePath = path.toAbsolutePath().toString();
        String mask = "9.99.999.9999";

        GrupoResponse grupoResponse = new GrupoResponse(new ArrayList<>());
        fileProcessorService.loadJsonFromFile(filePath, mask);

        GrupoResponse globalJson = fileProcessorService.getGlobalJson();

        assertNotNull(globalJson);
        assertEquals(grupoResponse.getGrupos(), globalJson.getGrupos());
    }

    @Test
    public void testFilterByClassificacao() {
        TreeNode rootNode = new TreeNode("3", "teste");
        TreeNode rootNode2 = new TreeNode("3.1", "teste2");
        rootNode.addGrupo(rootNode2);
        List<TreeNode> treeNodeList = new ArrayList<>();
        treeNodeList.add(rootNode);

        GrupoResponse result = fileProcessorService.filterByClassificacao(treeNodeList, "3");

        assertNotNull(result);
        assertEquals(1, result.getGrupos().size());
        assertEquals("3", result.getGrupos().get(0).getClassificacao());
        assertEquals("teste", result.getGrupos().get(0).getDescricao());

        result = fileProcessorService.filterByClassificacao(treeNodeList, "3.1");
        assertEquals("3.1", result.getGrupos().get(0).getClassificacao());
        assertEquals("teste2", result.getGrupos().get(0).getDescricao());
    }

    @Test
    public void testFilterByClassificacaoJsonNull() {
        GrupoResponse result = fileProcessorService.filterByClassificacao(null, "3");

        assertNotNull(result);
        assertEquals(0, result.getGrupos().size());
    }

    @Test
    public void testFilterGrupoByClassificacaoReturnNull() {
        TreeNode rootNode = new TreeNode("2", "teste");
        List<TreeNode> treeNodeList = new ArrayList<>();
        treeNodeList.add(rootNode);
        GrupoResponse grupoResponse = new GrupoResponse(new ArrayList<>());

        GrupoResponse result = fileProcessorService.filterByClassificacao(treeNodeList, "3");

        assertEquals(grupoResponse.getGrupos(), result.getGrupos());
    }

    @Test
    public void testGetGlobalJson() throws IOException {
        Path path = Paths.get("arquivos/teste.txt");
        String filePath = path.toAbsolutePath().toString();
        String mask = "9.99.999.9999";

        fileProcessorService.loadJsonFromFile(filePath, mask);

        assertNotNull(fileProcessorService.getGlobalJson());
    }
}
