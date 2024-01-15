package com.example.horapp;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
public class CustomViewHolder extends RecyclerView.ViewHolder{
    public TextView TV1, TV2;
    public ImageView IV1;
    public CardView cardView;
    public CustomViewHolder(@NonNull View itemView){
        super(itemView);
        TV1 = itemView.findViewById(R.id.TV1);
        TV2 = itemView.findViewById(R.id.TV1_0);
        IV1 = itemView.findViewById(R.id.IV1);
        cardView = itemView.findViewById(R.id.main_container);
    }
}