package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Helpline extends AppCompatActivity{
    private static final int REQUEST_CALL = 1;
    private ArrayList<ExampleItem> mExampleList;
    private BottomNavigationView bottomNavigationView;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private TextView helplineWord;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Helpline.this, Dashboard.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);

        helplineWord = findViewById(R.id.helpline_pageTitle);
        Shader textShader = new LinearGradient(0, 0,helplineWord.getPaint().measureText(helplineWord.getText().toString()),helplineWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        helplineWord.getPaint().setShader(textShader);
        helplineWord.setTextColor(Color.argb(255,0,0,0));

        bottomNavigationView = findViewById(R.id.navigationBar_helpline);
        bottomNavigationView.setSelectedItemId(R.id.helpline);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent i = null;

                if(item.getItemId()==R.id.home){
                    i = new Intent(Helpline.this,Dashboard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                else if(item.getItemId()==R.id.learn) {

                    i = new Intent(Helpline.this, Learn.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                else if(item.getItemId()==R.id.forum){
                    i = new Intent(Helpline.this, Forum.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                else if(item.getItemId()==R.id.messaging){

                    i = new Intent(Helpline.this,ChatList.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    //overridePendingTransition(20,20);
                }


                return true;
            }
        });


        createExampleList();
        buildRecyclerView();
    }



    public void makePhoneCall(String phone) {
        String number = phone;

        if (ContextCompat.checkSelfPermission(Helpline.this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Helpline.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Check if there is an activity that can handle the intent before starting it
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where there is no activity to handle the webpage
            Toast.makeText(this, "No app to handle webpage", Toast.LENGTH_SHORT).show();
        }
    }


    public void createExampleList() {
        mExampleList = new ArrayList<>();
        mExampleList.add(new ExampleItem(R.drawable.worg1, "Women's Aid Organisation", "https://wao.org.my/about-us/", "Women's Aid Organisation is a Malaysian non-governmental organisation that fights for women's rights and specifically against violence against women.", "+603 3000 8858"));
        mExampleList.add(new ExampleItem(R.drawable.worg2, "United Nations Development Fund for Women", "https://www.unwomen.org/", "The United Nations Development Fund for Women was established in December 1976 originally as the Voluntary Fund for the United Nations Decade for Women in the International Women's Year. Its first director was Margaret C. Snyder.", "+1 212 906 6400"));
        mExampleList.add(new ExampleItem(R.drawable.worg3, "Women for Women International", "https://www.womenforwomen.org/", "Women for Women International is a nonprofit humanitarian organization that provides practical and moral support to female survivors of war.", "1 (888) 504-3247"));
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);

    

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
/*                ExampleItem clickedItem = mExampleList.get(position);
                String url = clickedItem.getText2();

                // Add "http://" to the link
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }

                // Open the link in a web browser
                openWebPage(url);*/
            }


            @Override
            public void onDeleteClick(int position) {
                makePhoneCall(mExampleList.get(position).getPhone());
            }
        });
    }



}
