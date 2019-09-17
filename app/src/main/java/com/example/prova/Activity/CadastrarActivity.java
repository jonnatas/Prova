package com.example.prova.Activity;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prova.MaskWatcher;
import com.example.prova.Model.Empresa;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastrarActivity extends AppCompatActivity {

    private TextInputEditText textInputNome;

    private TextInputLayout textInputLayoutNomeEmpresa;
    private TextInputLayout textInputLayoutEstado;
    private TextInputLayout textInputLayoutCep;
    private TextInputLayout textInputLayoutEndereco;

    private TextInputEditText textInputCEP;
    private TextInputEditText textInputEndereco;
    private AutoCompleteTextView filledExposedDropdownEstado;

    private Button buttonCadastrar;
    private Button buttonCancelar;

    private RetrofitConfig retrofitConfig;

    private RadioGroup radioGroupSegmento;
    private RadioButton radioButtonEscolhido;

    private String segmentoSelecionado;
    private String siglaUFSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        configurarRetrofit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova empresa");
        getSupportActionBar().setHomeButtonEnabled(true);

        instanciandoWidgets(this);

        criarRadioGroupSegmento();
        cadastrarEmpresa();
        selecionarEstado(this);

    }

    private void instanciandoWidgets(CadastrarActivity cadastrarActivity) {
        textInputLayoutNomeEmpresa = findViewById(R.id.textInputLayoutNameId);
        textInputLayoutEstado = findViewById(R.id.textInputLayoutEstadoId);
        textInputLayoutEndereco = findViewById(R.id.textInputLayoutEnderecoId);
        textInputLayoutCep = findViewById(R.id.textInputLayoutCepId);

        textInputNome = findViewById(R.id.textInputNameId);
        textInputEndereco = findViewById(R.id.textInputEnderecoId);

        textInputCEP = findViewById(R.id.textInputCepId);

        textInputCEP.addTextChangedListener(new MaskWatcher("##.###-##"));

        filledExposedDropdownEstado = findViewById(R.id.filled_exposed_dropdown);
        radioGroupSegmento = findViewById(R.id.radioGroupSegmento);

        buttonCadastrar = findViewById(R.id.buttonAdicionarEmpresaId);
        buttonCancelar = findViewById(R.id.buttonCancelarAdicionarEmpresaId);
    }

    private void configurarRetrofit() {
        //Instanciando o retrofit para a comunicação com a API
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);
    }

    private void selecionarEstado(CadastrarActivity cadastrarActivity) {
        final List<String> listaDeSiglas = Arrays.asList(getResources().getStringArray(R.array.listaDeSiglas));
        final List<String> listaDeEstados = Arrays.asList(getResources().getStringArray(R.array.listaDeEstados));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(cadastrarActivity, R.layout.dropdown_menu_popup_item, listaDeEstados);
        filledExposedDropdownEstado.setAdapter(adapter);

        filledExposedDropdownEstado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int indiceEstado = listaDeEstados.indexOf(filledExposedDropdownEstado.getText().toString());
                siglaUFSelecionada = listaDeSiglas.get(indiceEstado);
            }
        });
    }

    private void criarRadioGroupSegmento() {
        //Controlando comportamento do RadioGroup
        int segmentoCheckedId = radioGroupSegmento.getCheckedRadioButtonId();
        if (segmentoCheckedId == R.id.radioButtonCarga) {
            segmentoSelecionado = getResources().getString(R.string.carga);
        } else if (segmentoCheckedId == R.id.radioButtonRodoviario) {
            segmentoSelecionado = getResources().getString(R.string.rodoviario);
        }
        radioGroupSegmento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int position) {
                radioButtonEscolhido = findViewById(radioGroup.getCheckedRadioButtonId());
                segmentoSelecionado = radioButtonEscolhido.getText().toString();
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

    private boolean validarCEP(TextInputLayout textInputLayout, String nomeEmpresa, String nome) {
        try {
            if (nomeEmpresa.length() != 10) {
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

    private void cadastrarEmpresa() {
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeEmpresa = textInputNome.getText().toString();
                String cepEmpresa = textInputCEP.getText().toString();
                String enderecoEmpresa = textInputEndereco.getText().toString();

                boolean nomeIsValido = validarCampo(textInputLayoutNomeEmpresa, nomeEmpresa, "Nome");
                boolean estadoIsValido = validarCampo(textInputLayoutEstado, siglaUFSelecionada, "Estado");
                boolean enderecoIsValido = validarCampo(textInputLayoutEndereco, enderecoEmpresa, "Endereço");
                boolean cepIsValido = validarCEP(textInputLayoutCep, cepEmpresa, "CEP");

                if (nomeIsValido && estadoIsValido && cepIsValido && enderecoIsValido) {
                    Empresa empresa = new Empresa(
                            nomeEmpresa,
                            segmentoSelecionado,
                            cepEmpresa,
                            siglaUFSelecionada,
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
            }
        });
    }
}
