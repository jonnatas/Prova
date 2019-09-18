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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Helper.SwipeDeleteCarro;
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
    private CarroAdapter carroAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextInputLayout textInputLayoutPlaca;
    private TextInputLayout textInputLayoutNumEixos;

    private TextInputEditText textInputPlaca;
    private TextInputEditText textInputNumEixos;

    private Button buttonCadastrarCarro;
    private Button buttonCancelarCarro;

    private Bundle extras;
    private Activity activity;

    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);
        //Carregando informações
        extras = getIntent().getExtras();
        activity = this;

        populateSupportActionBar();

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);


        instanciandoWidgets();

        //Inicializando os componentes da aplização
        confirmarExclusao(this);
        cadastrarCarro(this);
        carregarCarros(this);

        buttonCancelarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

    }

    // Inseirndo nome na ActionBar
    private void populateSupportActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(extras.getString("nome"));
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void instanciandoWidgets() {
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

    // Instanciando Alerta para confirmar a exclusão
    private void confirmarExclusao(CarroActivity carroActivity) {
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(carroActivity)
                .setTitle("Apagar empresa")
                .setMessage("confirmar exclusão")
                .setNegativeButton("Não", null)
                .setPositiveButton("SIM", apagarEmpresa());
    }

    //Excluir a empresa
    private DialogInterface.OnClickListener apagarEmpresa() {
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

                        //Inerindo a Flag de deletar empresa com a posição do item para atualizar na lista de empresas.
                        Intent resultIntent = new Intent();
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

    //Instanciando o menu da Toolbar
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

    /**
     * Recebendo e tratando a edição das informações da empresa na classe CadastroActivity.
     * e devolvendo a flag de Edição com a posição editada para atualizar na lista na MainActivity
     *
     * @param requestCode
     * @param resultCode
     * @param resultIntent
     */
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

    private void carregarCarros(final Activity activity) {
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
                carroAdapter = new CarroAdapter(carros, activity);
                recyclerView.setAdapter(carroAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDeleteCarro(carroAdapter, retrofitConfig, activity));
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }

            @Override
            public void onFailure(Call<ListCarro> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Cadastrar um novo veiculo.
     */
    private void cadastrarCarro(final Activity activity) {

        buttonCadastrarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placa = textInputPlaca.getText().toString();
                String numerosEixos = textInputNumEixos.getText().toString();

                boolean numEixosIsValido = validarNumEixos(numerosEixos);
                boolean placaIsValido = validarPlaca(placa);

                if (numEixosIsValido && placaIsValido) {
                    int idEmpresa = extras.getInt("idEmpresa");
                    final Carro carro = new Carro(placa, Integer.valueOf(numerosEixos), idEmpresa);
                    Call<Carro> carroCall = retrofitConfig.cadastrarCarro(carro);
                    try {
                        carroCall.enqueue(new Callback<Carro>() {
                            @Override
                            public void onResponse(Call<Carro> call, Response<Carro> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Carro responseCarro = response.body();
                                if (responseCarro.getSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Veiculo cadastrado com sucesso.", Toast.LENGTH_LONG).show();
                                    carroAdapter.adicionarItemPosition(responseCarro.getCarro(), 0);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error, o veiculo já existe! ", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Carro> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error ao cadastrar o veiculo", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /***
     * Validação da placa considerando que uma placa tem 7 digitos.
     * @param placa
     * @return
     */
    private boolean validarPlaca(String placa) {

        try {
            if (placa.isEmpty()) {
                textInputLayoutPlaca.setError("Número de eixos inválido!");
                return false;
            } else {
                if (placa.length() != 7) {
                    textInputLayoutPlaca.setError("Número de eixos inválido!");
                    return false;
                }
                textInputLayoutPlaca.setError("");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            textInputLayoutPlaca.setError("Número de eixos inválido!");
            return false;
        }
    }

    /***
     * Validação do numero de eixos, sempre maior que 0.
     * @param numerosEixos
     * @return
     */
    private boolean validarNumEixos(String numerosEixos) {
        try {
            if (numerosEixos.isEmpty()) {
                textInputLayoutNumEixos.setError("Número de eixos inválido!");
                return false;
            } else {
                if (numerosEixos.equals("0")) {
                    textInputLayoutNumEixos.setError("Número de eixos inválido!");
                    return false;
                }
                textInputLayoutNumEixos.setError("");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            textInputLayoutNumEixos.setError("Número de eixos inválido!");
            return false;
        }
    }
}
