package org.example.grouptree.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    @JsonProperty("classificacao")
    private String classificacao;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("grupos")
    private List<TreeNode> grupos;

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

    // Metodo para encontrar um grupo baseado na classificacao
    public TreeNode getGrupoByClassificacao(String classificacao) {
        for (TreeNode grupo : grupos) {
            if (grupo.getClassificacao().equals(classificacao)) {
                return grupo;
            }
        }
        return null;
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
