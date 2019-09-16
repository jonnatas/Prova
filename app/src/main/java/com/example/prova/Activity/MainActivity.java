package com.example.prova.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.prova.Model.Adapter.EmpresaAdapter;
import com.example.prova.Model.ListEmpresa;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    private ListEmpresa empresas;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter empresaAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder()
                .baseUrl("https://prova.cnt.org.br/XD01/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

        recyclerView = findViewById(R.id.recyclerViewEmpresasId);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        carregarEmpresas();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CadastrarActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEmpresas();
    }

    private void carregarEmpresas() {
        Call<ListEmpresa> call = retrofitConfig.carregarEmpresas();
        call.enqueue(new Callback<ListEmpresa>() {
            @Override
            public void onResponse(Call<ListEmpresa> call, Response<ListEmpresa> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                empresas = response.body();
                empresaAdapter = new EmpresaAdapter(empresas);
                recyclerView.setAdapter(empresaAdapter);
            }

            @Override
            public void onFailure(Call<ListEmpresa> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}