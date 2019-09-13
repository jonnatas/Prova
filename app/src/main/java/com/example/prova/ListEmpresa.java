package com.example.prova;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListEmpresa {
    @SerializedName("data")
    private List<Empresa> empresaList;

    public List<Empresa> getEmpresaList() {
        return empresaList;
    }

    public void setEmpresaList(List<Empresa> empresaList) {
        this.empresaList = empresaList;
    }

    public ListEmpresa(List<Empresa> empresaList) {
        this.empresaList = empresaList;
    }
}
