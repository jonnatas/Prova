package com.example.prova.Model.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Carro;
import com.example.prova.Model.ListCarro;
import com.example.prova.R;

import java.util.Collections;
import java.util.List;

public class CarroAdapter extends RecyclerView.Adapter<CarroAdapter.CarroViewHolder> {
    private List<Carro> carrosList;

    public CarroAdapter(ListCarro carrosList) {
        List<Carro> list = carrosList.getCarroList();
        Collections.reverse(list);
        this.carrosList = list;
    }

    @NonNull
    @Override
    public CarroAdapter.CarroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.carro_item, parent, false);
        CarroAdapter.CarroViewHolder holder = new CarroAdapter.CarroViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarroAdapter.CarroViewHolder holder, int position) {
        final Carro carro = carrosList.get(position);
        holder.textViewPlaca.setText(carro.getPlaca());
        int numEixos = carro.getNumeroEixos() * 1250;
        holder.textViewNumEixos.setText(numEixos + " Kg");

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), CarroActivity.class);
//                intent.putExtra("idEmpresa", empresa.getIdEmpresa());
//                intent.putExtra("nome", empresa.getNome());
//                intent.putExtra("segmento", empresa.getSegmento());
//                intent.putExtra("cep", empresa.getCep());
//                intent.putExtra("estado", empresa.getEstado());
//                intent.putExtra("endereco", empresa.getEndereco());
//                view.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return carrosList.size();
    }

    public class CarroViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPlaca;
        TextView textViewNumEixos;

        public CarroViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlaca = itemView.findViewById(R.id.textViewPlacaId);
            textViewNumEixos = itemView.findViewById(R.id.textViewNumEixosId);
        }
    }
}
