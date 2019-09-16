package com.example.prova.Model;

import android.content.Intent;

public class Carro {
    private String placa;
    private Integer numeroEixos;
    private Integer idVeiculo;
    private Integer idEmpresa;

    public Carro(String placa, Integer numeroEixos, Integer idEmpresa) {
        this.placa = placa;
        this.numeroEixos = numeroEixos;
        this.idEmpresa = idEmpresa;
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
