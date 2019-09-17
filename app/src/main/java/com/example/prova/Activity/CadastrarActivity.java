package com.example.prova.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    static final int UPDATE_EMPRESA = 1;  // The request code
    static final int UPDATE_EMPRESA_SUCESS = 10;  // The request code
    static final int DELETE_EMPRESA_SUCESS = 20;  // The request code
    static final int EDITED_EMPRESA_SUCESS = 30;  // The request code

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

    private Integer idEmpresa;
    private String segmentoSelecionado;
    private String siglaUFSelecionada;
    private List<String> listaDeSiglas;
    private List<String> listaDeEstados;
    private Bundle extras;

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
        filledExposedDropdownEstado = findViewById(R.id.filled_exposed_dropdown);

        radioGroupSegmento = findViewById(R.id.radioGroupSegmento);

        textInputCEP.addTextChangedListener(new MaskWatcher("##.###-##"));

        buttonCadastrar = findViewById(R.id.buttonAdicionarEmpresaId);
        buttonCancelar = findViewById(R.id.buttonCancelarAdicionarEmpresaId);

        listaDeSiglas = Arrays.asList(getResources().getStringArray(R.array.listaDeSiglas));
        listaDeEstados = Arrays.asList(getResources().getStringArray(R.array.listaDeEstados));

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retornar para Activity
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });
        preencherFormEditarEmpresa();
    }

    private void preencherFormEditarEmpresa() {
        extras = getIntent().getExtras();
        if (extras != null) {
            Toast.makeText(getApplicationContext(), "position: " + extras.getInt("position"), Toast.LENGTH_SHORT).show();

            if (extras.containsKey("idEmpresa")) {
                idEmpresa = extras.getInt("idEmpresa");
                textInputNome.setText(extras.getString("nome"));
                textInputEndereco.setText(extras.getString("endereco"));
                textInputCEP.setText(extras.getString("cep"));

                int indiceEstado = listaDeSiglas.indexOf(extras.getString("estado"));
                siglaUFSelecionada = listaDeSiglas.get(indiceEstado);
                String estadoSelecionado = listaDeEstados.get(indiceEstado);
                filledExposedDropdownEstado.setText(estadoSelecionado);

                String segmento = extras.getString("segmento");
                if (segmento.equals(getResources().getString(R.string.carga))) {
                    radioGroupSegmento.check(R.id.radioButtonCarga);
                } else if (segmento.equals(getResources().getString(R.string.rodoviario))) {
                    radioGroupSegmento.check(R.id.radioButtonRodoviario);
                }
            }
        }
    }

    private void configurarRetrofit() {
        //Instanciando o retrofit para a comunicação com a API
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);
    }

    private void selecionarEstado(CadastrarActivity cadastrarActivity) {

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
                    //Verificar se tem um id para editar
                    final Empresa empresa;
                    Call<Empresa> call;
                    if (idEmpresa != null) {
                        empresa = new Empresa(idEmpresa, nomeEmpresa, segmentoSelecionado, cepEmpresa, siglaUFSelecionada, enderecoEmpresa);
                        call = retrofitConfig.editarEmpresa(empresa);
                    } else {
                        empresa = new Empresa(nomeEmpresa, segmentoSelecionado, cepEmpresa, siglaUFSelecionada, enderecoEmpresa);
                        call = retrofitConfig.cadastrarEmpresa(empresa);
                    }

                    call.enqueue(new Callback<Empresa>() {
                        @Override
                        public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }

                            //Retornar para Activity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("idEmpresa", empresa.getIdEmpresa());
                            resultIntent.putExtra("nome", empresa.getNome());
                            resultIntent.putExtra("segmento", empresa.getSegmento());
                            resultIntent.putExtra("cep", empresa.getCep());
                            resultIntent.putExtra("estado", empresa.getEstado());
                            resultIntent.putExtra("endereco", empresa.getEndereco());
                            int position = extras.getInt("position");
                            resultIntent.putExtra("position", position);

                            setResult(UPDATE_EMPRESA_SUCESS, resultIntent);
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
