package com.example.prova;

import androidx.annotation.NonNull;

public class Empresa {
    private int idEmpresa;
    private String nome;
    private String Segmento;
    private String CEP;
    private String Estado;
    private String Endereco;

    public Empresa(String nome, String segmento, String CEP, String estado, String endereco) {
        this.nome = nome;
        Segmento = segmento;
        this.CEP = CEP;
        Estado = estado;
        Endereco = endereco;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSegmento() {
        return Segmento;
    }

    public void setSegmento(String segmento) {
        Segmento = segmento;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getEndereco() {
        return Endereco;
    }

    public void setEndereco(String endereco) {
        Endereco = endereco;
    }

    @NonNull
    @Override
    public String toString() {
        return getNome();
    }
}
