package com.example.prova.Model;

import com.google.gson.annotations.SerializedName;

public class Carro {
    private String placa;
    private Integer numeroEixos;
    private Integer idVeiculo;
    private Integer idEmpresa;

    @SerializedName("data")
    private Carro carro;

    private Boolean success;

    public Carro(String placa, Integer numeroEixos, Integer idEmpresa) {
        this.placa = placa;
        this.numeroEixos = numeroEixos;
        this.idEmpresa = idEmpresa;
    }

    public Carro(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Carro(Carro carro) {
        this.carro = carro;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getNumeroEixos() {
        return numeroEixos;
    }

    public void setNumeroEixos(Integer numeroEixos) {
        this.numeroEixos = numeroEixos;
    }

    public Integer getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Integer idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    @Override
    public String toString() {
        return "Placa:" + placa + " Carga Máxima: " + numeroEixos * 1250 + " Kg";
    }
}
