package com.example.prova.Model.Adapter;

import android.content.Intent;
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
    private List<Empresa> empresaList;

    public EmpresaAdapter(ListEmpresa empresaList) {
        List<Empresa> list = empresaList.getEmpresaList();
        Collections.reverse(list);
        this.empresaList = list;
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
