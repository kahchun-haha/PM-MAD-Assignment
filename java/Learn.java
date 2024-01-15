package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class Learn extends AppCompatActivity implements SelectListener{
    RecyclerView recyclerView;
    List<MyModel> myModelList;
    CustomAdapter customAdapter;
    SearchView searchView;

    private BottomNavigationView bottomNavigationView;

    private TextView learnWord;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Learn.this, Dashboard.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        learnWord = findViewById(R.id.learn_pageTitle);
        Shader textShader = new LinearGradient(0, 0,learnWord.getPaint().measureText(learnWord.getText().toString()),learnWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        learnWord.getPaint().setShader(textShader);
        learnWord.setTextColor(Color.argb(255,0,0,0));

        bottomNavigationView = findViewById(R.id.learn_navigationBar);
        bottomNavigationView.setSelectedItemId(R.id.learn);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i = null;

                if(item.getItemId()==R.id.home){
                    i = new Intent(Learn.this,Dashboard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                else if(item.getItemId()==R.id.helpline) {

                    i = new Intent(Learn.this, Helpline.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }


                else if(item.getItemId()==R.id.forum){
                    i = new Intent(Learn.this, Forum.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                else if(item.getItemId()==R.id.messaging){

                    i = new Intent(Learn.this,ChatList.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    //overridePendingTransition(20,20);
                }

                return true;
            }
        });

        searchView = findViewById(R.id.search_view);
        displayItems();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }
    private void filter(String newText) {
        List<MyModel> filteredList = new ArrayList<>();
        for(MyModel item: myModelList){
            if(item.getTopic().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        customAdapter.filterList(filteredList);
    }

    private void displayItems() {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        myModelList = new ArrayList<>();
        myModelList.add(new MyModel("Selangor records increase in domestic violence cases since 2017 - Police", "  ", R.drawable.dv2));
        myModelList.add(new MyModel("Selangor records highest number of domestic violence cases", "  ", R.drawable.dv3));
        myModelList.add(new MyModel("Pregnant Sabah woman alleges domestic violence by ‘VVIP’ husband", "  ", R.drawable.dv4));
        myModelList.add(new MyModel("Over 19k domestic violence cases reported in past three years",  "  ", R.drawable.dv5));
        myModelList.add(new MyModel("Gender-Based Violence And Domestic Violence On The Rise in Malaysia: A Growing Concern", "  ", R.drawable.dv6));
        myModelList.add(new MyModel("Woman in alleged domestic violence case urges police for fair probe", "  ", R.drawable.dv7));
        myModelList.add(new MyModel("Cultural shift needed to reduce domestic violence", "  ", R.drawable.dv8));
        myModelList.add(new MyModel("Alarming domestic violence statistics", "  ", R.drawable.dv9));

        customAdapter = new CustomAdapter(this, myModelList, this);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void onItemClicked(MyModel myModel) {
        Toast.makeText(this, myModel.getTopic(), Toast.LENGTH_SHORT).show();
    }

}