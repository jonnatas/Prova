package com.example.prova;

import com.example.prova.Model.Carro;
import com.example.prova.Model.Empresa;
import com.example.prova.Model.ListCarro;
import com.example.prova.Model.ListEmpresa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitConfig {

    //Lista todas as empresas
    @GET("empresa/listar")
    Call<ListEmpresa> carregarEmpresas();

    //Consulta empresa por id
    @GET("empresa/consulta/{idEmpresa}")
    Call<Empresa> consultarEmpresaPorId(@Path("idEmpresa") Integer idEmpresa);

    //Método para cadastro de empresa
    @POST("empresa/cadastrar")
    Call<Empresa> cadastrarEmpresa(@Body Empresa empresa);

    //Método para editar empresa
    @POST("empresa/editar")
    Call<Empresa> editarEmpresa(@Body Empresa empresa);

    //Método para deletar empresa
    @POST("empresa/delete")
    Call<Empresa> deleteEmpresa(@Body Empresa empresa);

    //Método para listar todos os veiculos da empresa
    @GET("empresa/{idEmpresa}/veiculo/listar")
    Call<ListCarro> listarVeiculos(@Path("idEmpresa") Integer idEmpresa);

    //Método para consultar um veiculo por idveiculo
    @GET("veiculo/consulta/{idVeiculo}")
    Call<Carro> consultaVeiculoPeloId(@Path("idVeiculo") Integer idVeiculo);

    //Método para cadastrar um veiculo
    @POST("veiculo/cadastrar")
    Call<Carro> cadastrarCarro(@Body Carro carro);

    //Método para editar um veiculo
    @POST("veiculo/editar")
    Call<Carro> editarCarro(@Body Carro carro);

    //Método para deletar um veiculo
    @POST("veiculo/delete")
    Call<Carro> deleteCarro(@Body String idVeiculo);

}
