package com.example.prova.Helper;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.Model.Adapter.EmpresaAdapter;
import com.example.prova.R;
import com.example.prova.RetrofitConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final RetrofitConfig retrofitConfig;
    private final Activity activity;
    private EmpresaAdapter empresaAdapter;
    private Drawable icon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(EmpresaAdapter empresaAdapter, RetrofitConfig retrofitConfig, Activity activity) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.empresaAdapter = empresaAdapter;
        icon = ContextCompat.getDrawable(activity, R.drawable.ic_delete_forever_white_24dp);
        background = new ColorDrawable(Color.RED);
        this.retrofitConfig = retrofitConfig;
        this.activity = activity;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconRight, iconTop, iconLeft, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        empresaAdapter.removeItemPosition(position);
        confirmarExclusao(position);
    }

    //Excluir a empresa
    private DialogInterface.OnClickListener apagarEmpresa(final int position) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                empresaAdapter.removeItemRetrofit(position, retrofitConfig);
            }
        };
    }

    // Instanciando Alerta para confirmar a exclusão
    private void confirmarExclusao(int position) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(activity)
                .setTitle("Apagar empresa permanentemente?")
                .setMessage("confirmar exclusão")
                .setCancelable(false)
                .setNegativeButton("Não", undoDelete(position))
                .setPositiveButton("SIM", apagarEmpresa(position));
        materialAlertDialogBuilder.show();
    }

    private DialogInterface.OnClickListener undoDelete(final int position) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                empresaAdapter.undoDelete();
            }
        };
    }
}
