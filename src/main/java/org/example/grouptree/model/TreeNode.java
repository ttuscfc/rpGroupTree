package org.example.grouptree.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private String classificacao;
    private String descricao;
    private List<TreeNode> grupos;

    // Construtor com 2 parâmetros: classificacao e descricao
    public TreeNode(String classificacao, String descricao) {
        this.classificacao = classificacao;
        this.descricao = descricao;
        this.grupos = new ArrayList<>();
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<TreeNode> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<TreeNode> grupos) {
        this.grupos = grupos;
    }

    public void addGrupo(TreeNode grupo) {
        this.grupos.add(grupo);
    }

    // Método para encontrar um grupo baseado na classificação
    public TreeNode getGrupoByClassificacao(String classificacao) {
        for (TreeNode grupo : grupos) {
            if (grupo.getClassificacao().equals(classificacao)) {
                return grupo;
            }
        }
        return null;
    }

    // Método para adicionar ou atualizar um grupo, baseado na classificação
    public TreeNode addOrUpdateGrupo(String classificacao, String descricao) {
        TreeNode grupo = getGrupoByClassificacao(classificacao);
        if (grupo == null) {
            grupo = new TreeNode(classificacao, descricao);
            addGrupo(grupo);
        } else {
            grupo.setDescricao(descricao);  // Se já existe, atualiza a descrição
        }
        return grupo;
    }

    @Override
    public String toString() {
        return "{" +
                "\"classificacao\": \"" + classificacao + "\", " +
                "\"descricao\": \"" + descricao + "\", " +
                "\"grupos\": " + grupos +
                "}";
    }
}
