package com.example.prova.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Adapter.CarroAdapter;
import com.example.prova.Model.Carro;
import com.example.prova.Model.Empresa;
import com.example.prova.Model.ListCarro;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private int idEmpresa;

    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Carregando informações
        extras = getIntent().getExtras();
        idEmpresa = extras.getInt("idEmpresa");

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

        confirmarExclusao(this);
        cadastrarCarro();
        carregarCarros();

    }

    private void confirmarExclusao(CarroActivity carroActivity) {
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(carroActivity)
                .setTitle("Apagar empresa")
                .setMessage("confirmar exclusão")
                .setNegativeButton("Não", null)
                .setPositiveButton("SIM", apagarCarro());
    }

    private DialogInterface.OnClickListener apagarCarro() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Empresa empresa = new Empresa(idEmpresa);
                Call<Empresa> call = retrofitConfig.deleteEmpresa(empresa);
                call.enqueue(new Callback<Empresa>() {
                    @Override
                    public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Empresa> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Erro ao deletar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu_carro_activity:
                materialAlertDialogBuilder.show();
//                        new AlertDialog.Builder(getApplicationContext());
//                dialog.setTitle("Apagar o carro");
//                dialog.setMessage("Deseja apagar permanentemente o carro?");
//                dialog.setNegativeButton("Nâo", null);
//                dialog.setPositiveButton("Sim", null);
//                dialog.create();
                return (true);
            case R.id.search_menu_carro_activity:
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregarCarros() {
        Call<ListCarro> listCarroCall = retrofitConfig.listarVeiculos(idEmpresa);
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

                boolean numEixosIsValido = validarCampo(textInputLayoutNumEixos, numerosEixos, "Número de eixos");
                boolean placaIsValido = validarCampo(textInputLayoutPlaca, placa, "Placa");

                if (numEixosIsValido && placaIsValido) {
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
