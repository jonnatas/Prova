package com.example.prova.Model.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Carro;
import com.example.prova.Model.ListCarro;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarroAdapter extends RecyclerView.Adapter<CarroAdapter.CarroViewHolder> {
    private List<Carro> carrosList;
    private Carro mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private Activity activity;

    public CarroAdapter(ListCarro carrosList, Activity activity) {
        List<Carro> list = carrosList.getCarroList();
        Collections.reverse(list);
        this.carrosList = list;
        this.activity = activity;
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
    }

    @Override
    public int getItemCount() {
        return carrosList.size();
    }

    public void removeItemPosition(int position) {
        mRecentlyDeletedItem = carrosList.get(position);
        mRecentlyDeletedItemPosition = position;
        carrosList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, carrosList.size());
    }

    public void adicionarItemPosition(Carro carro, int position) {
        carrosList.add(0, carro);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, carrosList.size());
    }

    public void undoDelete() {
        carrosList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        notifyItemRangeChanged(mRecentlyDeletedItemPosition, carrosList.size());
    }

    public void removeItemRetrofit(int position, RetrofitConfig retrofitConfig) {
        Call<Carro> call = retrofitConfig.deleteCarro(mRecentlyDeletedItem);

        call.enqueue(new Callback<Carro>() {
            @Override
            public void onResponse(Call<Carro> call, Response<Carro> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CarroAdapter.this.activity.getApplicationContext(), "Falha ao deletar:" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CarroAdapter.this.activity.getApplicationContext(), "Empresa deletada comsucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Carro> call, Throwable t) {
                Toast.makeText(CarroAdapter.this.activity.getApplicationContext(), "Erro ao deletar", Toast.LENGTH_SHORT).show();
            }
        });

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
