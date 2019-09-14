package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastrarActivity extends AppCompatActivity {

    private TextInputEditText nomeTextInput;
    private EditText cepEditText;
    private EditText enderecoEditText;
    private Spinner spinnerEstado;

    private Button cadastrarButton;
    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    private RadioGroup radioGroupSegmento;
    private RadioButton radioButtonEscolhido;

    private String segmentoValue;
    private String estadoValue;
    List<String> listaDeSiglas;
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

        nomeTextInput = findViewById(R.id.textInputNameId);
        cepEditText = findViewById(R.id.editTextCepId);
        enderecoEditText = findViewById(R.id.editTextEnderecoId);
        spinnerEstado = findViewById(R.id.spinnerStadoId);

        radioGroupSegmento = findViewById(R.id.radioGroupSegmento);

        criarRadioGroupSegmento();
        criarSpinnerDeEstado(this);

        cadastrarButton = findViewById(R.id.buttonAdicionarEmpresaId);
        cadastrarEmpresa();
    }

    private void criarRadioGroupSegmento() {
        //Controlando comportamento do RadioGroup
        definirValorInicialDoSegemento();
        radioGroupSegmento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int position) {
                radioButtonEscolhido = findViewById(radioGroup.getCheckedRadioButtonId());
                segmentoValue = radioButtonEscolhido.getText().toString();
            }
        });
    }

    private void criarSpinnerDeEstado(CadastrarActivity cadastrarActivity) {
        //Definindo o comportamento do Autocomplete de estados
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(cadastrarActivity, R.array.listaDeEstados, R.layout.support_simple_spinner_dropdown_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);
        listaDeSiglas = Arrays.asList(getResources().getStringArray(R.array.listaDeSiglas));
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                estadoValue = listaDeSiglas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                estadoValue = listaDeSiglas.get(0);
            }
        });
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
                String nomeEmpresa = nomeTextInput.getText().toString();
                String cepEmpresa = cepEditText.getText().toString();
                String enderecoEmpresa = enderecoEditText.getText().toString();

                if (nomeEmpresa.isEmpty() || segmentoValue.isEmpty() || cepEmpresa.isEmpty() || enderecoEmpresa.isEmpty() || estadoValue.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Campo em branco", Toast.LENGTH_LONG).show();
                    return;
                }
                Empresa empresa = new Empresa(
                        nomeEmpresa,
                        segmentoValue,
                        cepEmpresa,
                        estadoValue,
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
