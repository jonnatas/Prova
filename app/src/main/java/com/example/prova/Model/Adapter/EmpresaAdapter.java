package com.example.prova.Model.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Activity.CarroActivity;
import com.example.prova.Model.Empresa;
import com.example.prova.Model.ListEmpresa;
import com.example.prova.R;

import java.util.Collections;
import java.util.List;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder> {
    static final int UPDATE_EMPRESA = 1;  // The request code

    private List<Empresa> empresaList;
    private Context context;
    private Activity activity;

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
        empresaList.remove(positionItemRemovido);
        notifyItemRemoved(positionItemRemovido);
        notifyItemRangeChanged(positionItemRemovido, empresaList.size());
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
