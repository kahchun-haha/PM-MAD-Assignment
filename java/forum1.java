package com.example.horapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class forum1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum1);

        Intent intent = getIntent();
        if (intent.hasExtra("topic_id")) {
            String topicId = intent.getStringExtra("topic_id");
            // Use the topicId to display the relevant information for this card
        }
    }
}