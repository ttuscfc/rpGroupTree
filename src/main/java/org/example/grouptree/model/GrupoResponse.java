package org.example.grouptree.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GrupoResponse {

    @JsonProperty("grupos")
    private List<TreeNode> grupos;

    public GrupoResponse() {}

    public GrupoResponse(List<TreeNode> grupos) {
        this.grupos = grupos;
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

    @Override
    public String toString() {
        return "{" +
                "\"grupos\": " + grupos +
                "}";
    }
}
