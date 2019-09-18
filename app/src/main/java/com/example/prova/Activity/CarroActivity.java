package com.example.prova.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    static final int UPDATE_EMPRESA = 1;  // The request code
    static final int UPDATE_EMPRESA_SUCESS = 10;  // The request code
    static final int DELETE_EMPRESA_SUCESS = 20;  // The request code
    static final int EDITED_EMPRESA_SUCESS = 30;  // The request code
    static final int EDIT_EMPRESA = 40;  // The request code

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

    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);
        //Carregando informações
        extras = getIntent().getExtras();

        populateSupportActionBar();

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

        instanciandoWidgets(this);
        confirmarExclusao(this);
        cadastrarCarro();
        carregarCarros();
        buttonCancelarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

    }

    private void populateSupportActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(extras.getString("nome"));
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void instanciandoWidgets(CarroActivity carroActivity) {
        recyclerView = findViewById(R.id.recyclerViewCarroId);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        buttonCadastrarCarro = findViewById(R.id.buttonSalvarCarroId);
        buttonCancelarCarro = findViewById(R.id.buttonCancelarCarroId);

        textInputLayoutPlaca = findViewById(R.id.textInputLayoutPlacaId);
        textInputLayoutNumEixos = findViewById(R.id.textInputLayoutNumEixosId);

        textInputPlaca = findViewById(R.id.textInputPlacaId);
        textInputNumEixos = findViewById(R.id.textInputNumEixosId);
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
                final Empresa empresa = new Empresa(extras.getInt("idEmpresa"));
                Call<Empresa> call = retrofitConfig.deleteEmpresa(empresa);
                call.enqueue(new Callback<Empresa>() {
                    @Override
                    public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Falha ao deletar:" + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("idEmpresaItemRemovido", empresa.getIdEmpresa());
                        resultIntent.putExtra("positionItemRemovido", extras.getInt("position"));
                        setResult(DELETE_EMPRESA_SUCESS, resultIntent);
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
                return true;

            case R.id.edit_menu_carro_activity:
                Intent intent = new Intent(getApplicationContext(), CadastrarActivity.class);
                intent.putExtras(extras);
                startActivityForResult(intent, EDIT_EMPRESA);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == EDIT_EMPRESA) {

            if (resultCode == UPDATE_EMPRESA_SUCESS) {
                extras = resultIntent.getExtras();
                //Retornar para Activity
                Intent intent = new Intent();
                intent.putExtras(extras);
                intent.putExtra("position", extras.getInt("position"));
                setResult(EDITED_EMPRESA_SUCESS, intent);
                finish();
            }
        }
    }

    private void carregarCarros() {
        int idEmpresa = extras.getInt("idEmpresa");
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

        buttonCadastrarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placa = textInputPlaca.getText().toString();
                String numerosEixos = textInputNumEixos.getText().toString();

                boolean numEixosIsValido = validarCampo(textInputLayoutNumEixos, numerosEixos, "Número de eixos");
                boolean placaIsValido = validarCampo(textInputLayoutPlaca, placa, "Placa");

                if (numEixosIsValido && placaIsValido) {
                    int idEmpresa = extras.getInt("idEmpresa");
                    Carro carro = new Carro(placa, Integer.valueOf(numerosEixos), idEmpresa);
                    Call<Carro> carroCall = retrofitConfig.cadastrarCarro(carro);
                    carroCall.enqueue(new Callback<Carro>() {
                        @Override
                        public void onResponse(Call<Carro> call, Response<Carro> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            Carro dataResponse = response.body();
                            Toast.makeText(getApplicationContext(), "PLACA: " + response.code(), Toast.LENGTH_LONG).show();
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
