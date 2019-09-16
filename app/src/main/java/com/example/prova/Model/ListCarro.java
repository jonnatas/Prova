package com.example.prova.Model;

import com.example.prova.Model.Carro;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCarro {
    @SerializedName("data")
    private List<Carro> carroList;

    public ListCarro(List<Carro> carroList) {
        this.carroList = carroList;
    }

    public List<Carro> getCarroList() {
        return carroList;
    }

    public void setCarroList(List<Carro> carroList) {
        this.carroList = carroList;
    }
}
