package com.example.prova.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Adapter.EmpresaAdapter;
import com.example.prova.Model.ListEmpresa;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    static final int UPDATE_EMPRESA = 1;  // The request code
    static final int UPDATE_EMPRESA_SUCESS = 10;  // The request code
    static final int DELETE_EMPRESA_SUCESS = 20;  // The request code
    static final int EDITED_EMPRESA_SUCESS = 30;  // The request code

    private Retrofit retrofit;
    private RetrofitConfig retrofitConfig;

    private ListEmpresa empresas;

    private RecyclerView recyclerViewEmpresas;
    private EmpresaAdapter empresaAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

        recyclerViewEmpresas = findViewById(R.id.recyclerViewEmpresasId);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewEmpresas.setLayoutManager(layoutManager);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CadastrarActivity.class);
                intent.putExtra("position", 0);
                startActivityForResult(intent, UPDATE_EMPRESA);
            }
        });
        carregarEmpresas(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_EMPRESA) {
            switch (resultCode) {
                case UPDATE_EMPRESA_SUCESS:
                    addItemNaLista(data.getExtras());
                    return;
                case Activity.RESULT_CANCELED:
//                    Toast.makeText(getApplicationContext(), "RESULT_CANCELED:" + resultCode + " requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
                    return;
                case DELETE_EMPRESA_SUCESS:
                    removerItemDeletadoDaLista(data.getExtras());
                    return;
                case EDITED_EMPRESA_SUCESS:
                    Toast.makeText(getApplicationContext(), "Empresa editada com sucesso.", Toast.LENGTH_SHORT).show();

                    atualizarItemNaLista(data.getExtras());
                    return;
            }
        }
    }

    private void atualizarItemNaLista(Bundle bundle) {
        empresaAdapter.atualizarItemPosition(bundle);

    }

    private void addItemNaLista(Bundle bundle) {
        empresaAdapter.adicionarItemPosition(bundle);
    }

    private void removerItemDeletadoDaLista(Bundle bundle) {
        int positionItemRemovido = bundle.getInt("positionItemRemovido");
        empresaAdapter.removeItemPosition(positionItemRemovido);
    }

    private void carregarEmpresas(final Activity activity) {
        Call<ListEmpresa> call = retrofitConfig.carregarEmpresas();
        call.enqueue(new Callback<ListEmpresa>() {
            @Override
            public void onResponse(Call<ListEmpresa> call, Response<ListEmpresa> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Falha:" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                empresas = response.body();
                empresaAdapter = new EmpresaAdapter(empresas, activity);
                recyclerViewEmpresas.setAdapter(empresaAdapter);
                empresaAdapter.setContext(getApplicationContext());
            }

            @Override
            public void onFailure(Call<ListEmpresa> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
