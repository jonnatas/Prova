package com.example.prova.Model;

public class Empresa {
    private int idEmpresa;
    private String nome;
    private String segmento;
    private String cep;
    private String estado;
    private String endereco;

    public Empresa(int idEmpresa, String nome, String segmento, String cep, String estado, String endereco) {
        this.idEmpresa = idEmpresa;
        this.nome = nome;
        this.segmento = segmento;
        this.cep = cep;
        this.estado = estado;
        this.endereco = endereco;
    }

    public Empresa(String nome, String segmento, String cep, String estado, String endereco) {
        this.nome = nome;
        this.segmento = segmento;
        this.cep = cep;
        this.estado = estado;
        this.endereco = endereco;
    }

    public Empresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
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
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
