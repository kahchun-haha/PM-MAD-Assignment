package com.example.horapp;

import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>{
    private Context context;
    private List<MyModel> list;
    private SelectListener listener;

    public CustomAdapter(Context context, List<MyModel> list, SelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.single_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.TV1.setText(list.get(position).getTopic());
        holder.TV2.setText(list.get(position).getDescription());
        holder.IV1.setImageResource(list.get(position).getImageResourceId());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onItemClicked(list.get(position));
//              activity_forum1();

                String topic = list.get(position).getTopic();
                switch (topic) {
                    case "Selangor records increase in domestic violence cases since 2017 - Police":
                        navigateToForum(forum1.class);
                        break;
                    case "Selangor records highest number of domestic violence cases":
                        navigateToForum(forum2.class);
                        break;
                    case "Pregnant Sabah woman alleges domestic violence by ‘VVIP’ husband":
                        navigateToForum(forum3.class);
                        break;
                    case "Over 19k domestic violence cases reported in past three years":
                        navigateToForum(forum4.class);
                        break;
                    case "Gender-Based Violence And Domestic Violence On The Rise in Malaysia: A Growing Concern":
                        navigateToForum(forum5.class);
                        break;
                    case "Woman in alleged domestic violence case urges police for fair probe":
                        navigateToForum(forum6.class);
                        break;
                    case "Cultural shift needed to reduce domestic violence":
                        navigateToForum(forum7.class);
                        break;
                    case "Alarming domestic violence statistics":
                        navigateToForum(forum8.class);
                        break;
                    default:
                        // Handle other cases or show an error message
                        break;
                }

            }
        });
    }

    private void navigateToForum(Class<?> forumClass) {
        Intent intent = new Intent(context, forumClass);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<MyModel> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }

/*    public void activity_forum1(){
        Intent intent = new Intent(this, forum1.class);
        startActivity(intent);
    }*/
}
