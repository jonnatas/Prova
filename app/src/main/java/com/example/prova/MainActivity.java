package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;
    private ListView listViewEmpresa;
    private ListEmpresa empresas;
    private Button buttonCadastrarEmpresa;
    private ArrayAdapter<Empresa> arrayAdapter;



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

        listViewEmpresa = findViewById(R.id.listViewEmpresasId);
        buttonCadastrarEmpresa = findViewById(R.id.buttonCadastrarEmpresaId);

        carregarEmpresas();
        cadastrarEmpresa();
        irParaEmpresa();
    }

    private void irParaEmpresa() {
        listViewEmpresa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Empresa empresa = empresas.getEmpresaList().get(position);
                Intent intent = new Intent(getApplicationContext(), CarroActivity.class);
                intent.putExtra("idEmpresa", empresa.getIdEmpresa());
                intent.putExtra("nome", empresa.getNome());
                intent.putExtra("segmento", empresa.getSegmento());
                intent.putExtra("CEP", empresa.getCEP());
                intent.putExtra("estado", empresa.getEstado());
                intent.putExtra("endereco", empresa.getEndereco());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEmpresas();
    }

    private void cadastrarEmpresa() {
        buttonCadastrarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CadastrarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void carregarEmpresas() {
        Call<ListEmpresa> call = retrofitConfig.carregarEmpresas();
        call.enqueue(new Callback<ListEmpresa>() {
            @Override
            public void onResponse(Call<ListEmpresa> call, Response<ListEmpresa> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Falha:"+response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "sucesso", Toast.LENGTH_LONG).show();
                empresas = response.body();
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, empresas.getEmpresaList());
                listViewEmpresa.setAdapter(arrayAdapter);

            }

            @Override
            public void onFailure(Call<ListEmpresa> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
