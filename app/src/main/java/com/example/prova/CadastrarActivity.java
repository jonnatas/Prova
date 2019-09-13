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

    private EditText nomeEditText;
    private EditText cepEditText;
    private EditText estadoEditText;
    private EditText enderecoEditText;

    private Button cadastrarButton;

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    private RadioGroup radioGroupSegmento;
    private RadioButton radioButtonEscolhido;

    private String segmentoValue;
    private Integer segmentoCheckedId;

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

        nomeEditText = findViewById(R.id.editTextNomeEmpresa);
        cepEditText = findViewById(R.id.editTextCepId);
        estadoEditText = findViewById(R.id.editTextEstadoId);
        enderecoEditText = findViewById(R.id.editTextEnderecoId);

        radioGroupSegmento = findViewById(R.id.radioGroupSegmento);
        //Controlando comportamento do RadioGroup
        definirValorInicialDoSegemento();

        radioGroupSegmento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int position) {
                radioButtonEscolhido = findViewById(radioGroup.getCheckedRadioButtonId());
                segmentoValue = radioButtonEscolhido.getText().toString();
            }
        });


        cadastrarButton = findViewById(R.id.buttonAdicionarEmpresaId);
        cadastrarEmpresa();
    }

    private void definirValorInicialDoSegemento() {
        segmentoCheckedId = radioGroupSegmento.getCheckedRadioButtonId();
        if (segmentoCheckedId == R.id.radioButtonCarga) {
            segmentoValue = getResources().getString(R.string.carga);
        } else if (segmentoCheckedId == R.id.radioButtonRodoviario) {
            segmentoValue = getResources().getString(R.string.rodoviario);
        }
    }

    private void cadastrarEmpresa() {
        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeEmpresa = nomeEditText.getText().toString();
                String cepEmpresa = cepEditText.getText().toString();
                String estadoEmpresa = estadoEditText.getText().toString();
                String enderecoEmpresa = enderecoEditText.getText().toString();

                if (nomeEmpresa.isEmpty() || segmentoValue.isEmpty() || cepEmpresa.isEmpty() || estadoEmpresa.isEmpty() || enderecoEmpresa.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Campo em branco", Toast.LENGTH_LONG).show();
                    return;
                }
                Empresa empresa = new Empresa(
                        nomeEmpresa,
                        segmentoValue,
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
