package com.example.prova;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitConfig {
    @POST("empresa/cadastrar")
    Call<Empresa> cadastrarEmpresa(@Body Empresa empresa);

    @GET("empresa/listar")
    Call<ListEmpresa> carregarEmpresas();

    @POST("veiculo/cadastrar")
    Call<Carro> cadastrarCarro(@Body Carro carro);

    @GET("empresa/{idEmpresa}/veiculo/listar")
    Call<ListCarro> carregarCarros(@Path("idEmpresa") Integer idEmpresa);
}
