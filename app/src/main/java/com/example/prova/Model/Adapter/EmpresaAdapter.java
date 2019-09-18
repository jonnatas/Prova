package com.example.prova.Model.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder> {
    static final int UPDATE_EMPRESA = 1;  // The request code

    private List<Empresa> empresaList;
    private Context context;
    private Activity activity;
    private Empresa mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public EmpresaAdapter(ListEmpresa empresaList, Activity activity) {
        List<Empresa> list = empresaList.getEmpresaList();
        Collections.reverse(list);
        this.empresaList = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public EmpresaViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.empresa_item, parent, false);
        return new EmpresaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaViewHolder holder, final int position) {
        final Empresa empresa = empresaList.get(position);
        holder.textViewSegmento.setText(empresa.getSegmento());
        holder.textViewEstado.setText(empresa.getEstado());
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
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, UPDATE_EMPRESA);
            }
        });

    }

    @Override
    public int getItemCount() {
        return empresaList.size();
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void removeItemPosition(int positionItemRemovido) {
        mRecentlyDeletedItem = empresaList.get(positionItemRemovido);
        mRecentlyDeletedItemPosition = positionItemRemovido;
        empresaList.remove(positionItemRemovido);
        notifyItemRemoved(positionItemRemovido);
        notifyItemRangeChanged(positionItemRemovido, empresaList.size());
//        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = activity.findViewById(R.id.coordinator_layout_main);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empresaList.add(mRecentlyDeletedItemPosition,
                        mRecentlyDeletedItem);
                notifyItemInserted(mRecentlyDeletedItemPosition);
                notifyItemRangeChanged(mRecentlyDeletedItemPosition, empresaList.size());
            }
        });
        snackbar.show();
    }

    public void adicionarItemPosition(Bundle bundle) {
        Empresa newEmpresa = new Empresa(
                bundle.getInt("idEmpresa"),
                bundle.getString("nome"),
                bundle.getString("segmento"),
                bundle.getString("cep"),
                bundle.getString("estado"),
                bundle.getString("endereco")
        );
        int position = bundle.getInt("position");

        empresaList.add(0, newEmpresa);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, empresaList.size());

    }

    public void undoDelete() {
        empresaList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        notifyItemRangeChanged(mRecentlyDeletedItemPosition, empresaList.size());
    }

    public void atualizarItemPosition(Bundle bundle) {
        Empresa newEmpresa = new Empresa(
                bundle.getInt("idEmpresa"),
                bundle.getString("nome"),
                bundle.getString("segmento"),
                bundle.getString("cep"),
                bundle.getString("estado"),
                bundle.getString("endereco")
        );
        int position = bundle.getInt("position");
        empresaList.remove(position);
        notifyItemRangeChanged(position, empresaList.size());
        empresaList.add(position, newEmpresa);
        notifyItemRangeChanged(position, empresaList.size());
    }

    public void removeItemRetrofit(int position, RetrofitConfig retrofitConfig) {
        Call<Empresa> call = retrofitConfig.deleteEmpresa(mRecentlyDeletedItem);

        call.enqueue(new Callback<Empresa>() {
            @Override
            public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EmpresaAdapter.this.activity.getApplicationContext(), "Falha ao deletar:" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(EmpresaAdapter.this.activity.getApplicationContext(), "Empresa deletada comsucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Empresa> call, Throwable t) {
                Toast.makeText(EmpresaAdapter.this.activity.getApplicationContext(), "Erro ao deletar", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class EmpresaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome;
        TextView textViewSegmento;
        TextView textViewEstado;

        public EmpresaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNomeCardId);
            textViewSegmento = itemView.findViewById(R.id.textViewSegmentoCardId);
            textViewEstado = itemView.findViewById(R.id.textViewEstadoId);
        }
    }
}
