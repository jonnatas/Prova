package com.example.prova.Model.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Activity.CarroActivity;
import com.example.prova.Model.Empresa;
import com.example.prova.Model.ListEmpresa;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder> {
    private RetrofitConfig retrofitConfig;
    private Retrofit retrofit;
    private Activity mActivity;
    private List<Empresa> empresaList;
    private Context context;
    private Empresa mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public EmpresaAdapter(ListEmpresa empresaList) {
        List<Empresa> list = empresaList.getEmpresaList();
        Collections.reverse(list);
        this.empresaList = list;
        //Instanciando o retrofit para a comunicação com a API
        retrofit = new Retrofit.Builder().baseUrl("https://prova.cnt.org.br/XD01/").addConverterFactory(GsonConverterFactory.create()).build();
        retrofitConfig = retrofit.create(RetrofitConfig.class);

    }

    @NonNull
    @Override
    public EmpresaViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.empresa_item, parent, false);
        EmpresaViewHolder holder = new EmpresaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaViewHolder holder, int position) {
        final Empresa empresa = empresaList.get(position);
        holder.textViewSegmento.setText(empresa.getSegmento());
        holder.textViewNome.setText(empresa.getNome());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CarroActivity.class);
                intent.putExtra("idEmpresa", empresa.getIdEmpresa());
                intent.putExtra("nome", empresa.getNome());
                intent.putExtra("segmento", empresa.getSegmento());
                intent.putExtra("cep", empresa.getCep());
                intent.putExtra("estado", empresa.getEstado());
                intent.putExtra("endereco", empresa.getEndereco());
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return empresaList.size();
    }

    public void deleteItem(final int position) {
        mRecentlyDeletedItem = empresaList.get(position);
        mRecentlyDeletedItemPosition = position;
        empresaList.remove(position);

        Call<Empresa> call = retrofitConfig.deleteEmpresa(mRecentlyDeletedItem);
        call.enqueue(new Callback<Empresa>() {
            @Override
            public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Call<Empresa> call, Throwable t) {
                Log.i("APAGOU", "Code: " + t.getMessage());
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

//    private void showUndoSnackbar() {
//        View view = mActivity.findViewById(R.id.coordinator_layout);
//        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text, Snackbar.LENGTH_LONG);
//        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
//        snackbar.show();
//    }
//
//    private void undoDelete() {
//        empresaList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
//        notifyItemInserted(mRecentlyDeletedItemPosition);
//    }

    public class EmpresaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome;
        TextView textViewSegmento;

        public EmpresaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNomeCardId);
            textViewSegmento = itemView.findViewById(R.id.textViewSegmentoCardId);
        }
    }
}
