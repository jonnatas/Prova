package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastrarActivity extends AppCompatActivity {

    private EditText nome;
    private EditText segmento;
    private EditText cep;
    private EditText estado;
    private EditText endereco;

    private Button cadastrarButton;

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder()
                .baseUrl("https://prova.cnt.org.br/XD01/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);
        nome = findViewById(R.id.editTextNomeEmpresa);
        segmento = findViewById(R.id.editTextSegmentoEmpresaId);
        cep = findViewById(R.id.editTextCepId);
        estado = findViewById(R.id.editTextEstadoId);
        endereco = findViewById(R.id.editTextEnderecoId);
        cadastrarButton = findViewById(R.id.buttonAdicionarEmpresaId);

        cadastrarEmpresa();

    }

    private void cadastrarEmpresa() {
        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeEmpresa = nome.getText().toString();
                String segmentoEmpresa = segmento.getText().toString();
                String cepEmpresa = cep.getText().toString();
                String estadoEmpresa = estado.getText().toString();
                String enderecoEmpresa = endereco.getText().toString();

                if (nomeEmpresa.isEmpty() || segmentoEmpresa.isEmpty() || cepEmpresa.isEmpty() || estadoEmpresa.isEmpty() || enderecoEmpresa.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Campo em branco", Toast.LENGTH_LONG).show();
                    return;
                }
                Empresa empresa = new Empresa(
                        nomeEmpresa,
                        segmentoEmpresa,
                        cepEmpresa,
                        estadoEmpresa,
                        enderecoEmpresa
                );
                Call<Empresa> call = retrofitConfig.cadastrarEmpresa(empresa);
                call.enqueue(new Callback<Empresa>() {
                    @Override
                    public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Empresa> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
