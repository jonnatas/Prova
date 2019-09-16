package com.example.prova.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Adapter.CarroAdapter;
import com.example.prova.Model.Carro;
import com.example.prova.Model.ListCarro;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarroActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    private ListCarro carros;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter carroAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextInputLayout textInputLayoutPlaca;
    private TextInputLayout textInputLayoutNumEixos;

    private TextInputEditText textInputPlaca;
    private TextInputEditText textInputNumEixos;

    private Button buttonCadastrarCarro;
    private Button buttonCancelarCarro;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Carregando informações
        extras = getIntent().getExtras();

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder()
                .baseUrl("https://prova.cnt.org.br/XD01/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

        recyclerView = findViewById(R.id.recyclerViewCarroId);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        buttonCadastrarCarro = findViewById(R.id.buttonSalvarCarroId);
        buttonCancelarCarro = findViewById(R.id.buttonCancelarCarroId);

        textInputLayoutPlaca = findViewById(R.id.textInputLayoutPlacaId);
        textInputLayoutNumEixos = findViewById(R.id.textInputLayoutNumEixosId);

        textInputPlaca = findViewById(R.id.textInputPlacaId);
        textInputNumEixos = findViewById(R.id.textInputNumEixosId);

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
                carroAdapter = new CarroAdapter(carros);
                recyclerView.setAdapter(carroAdapter);
            }

            @Override
            public void onFailure(Call<ListCarro> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cadastrarCarro() {
        buttonCancelarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonCadastrarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placa = textInputPlaca.getText().toString();
                String numerosEixos = textInputNumEixos.getText().toString();

                Integer idEmpresa = extras.getInt("idEmpresa");

                boolean numEixosIsValido = validarCampo(textInputLayoutNumEixos, numerosEixos, "Número de eixos");
                boolean placaIsValido = validarCampo(textInputLayoutPlaca, placa, "Placa");

                if (numEixosIsValido || placaIsValido) {
                    Carro carro = new Carro(placa, Integer.valueOf(numerosEixos), idEmpresa);
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
            }
        });
    }

    private boolean validarCampo(TextInputLayout textInputLayout, String nomeEmpresa, String nome) {
        try {
            if (nomeEmpresa.isEmpty()) {
                textInputLayout.setError(nome + " inválido");
                return false;
            } else {
                textInputLayout.setError("");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            textInputLayout.setError(nome + " inválido");
            return false;
        }
    }
}
