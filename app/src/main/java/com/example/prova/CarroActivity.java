package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarroActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;
    private ListView listViewCarro;
    private ListCarro carros;
    private Button buttonCadastrarCarro;
    private EditText editTextNumeroEixos;
    private EditText placaEditText;
    private ArrayAdapter<Carro> arrayAdapter;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);

        //Carregando informações
        extras = getIntent().getExtras();

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder()
                .baseUrl("https://prova.cnt.org.br/XD01/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

        listViewCarro = findViewById(R.id.listViewCarrosId);
        buttonCadastrarCarro = findViewById(R.id.buttonSalvarCarroId);

        editTextNumeroEixos = findViewById(R.id.editTextNumeroEixosId);
        placaEditText = findViewById(R.id.editTextPlacaId);

        cadastrarCarro();
        carregarCarros();

    }

    private void carregarCarros() {
        Call<ListCarro> listCarroCall = retrofitConfig.listarVeiculos(extras.getInt("idEmpresa"));
        listCarroCall.enqueue(new Callback<ListCarro>() {
            @Override
            public void onResponse(Call<ListCarro> call, Response<ListCarro> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                carros = response.body();
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, carros.getCarroList());
                listViewCarro.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<ListCarro> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cadastrarCarro() {
        buttonCadastrarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placa = placaEditText.getText().toString();
                String numerosEixos = editTextNumeroEixos.getText().toString();
                Integer idEmpresa = extras.getInt("idEmpresa");
                if (placa.isEmpty() || numerosEixos.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Campo em branco", Toast.LENGTH_LONG).show();
                    return;
                }
                Carro carro = new Carro(
                        placa,
                        Integer.valueOf(numerosEixos),
                        idEmpresa
                );
                Call<Carro> carroCall = retrofitConfig.cadastrarCarro(carro);
                carroCall.enqueue(new Callback<Carro>() {
                    @Override
                    public void onResponse(Call<Carro> call, Response<Carro> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "sucesso", Toast.LENGTH_LONG).show();
                        carregarCarros();
                    }

                    @Override
                    public void onFailure(Call<Carro> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
