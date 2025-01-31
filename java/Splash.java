package com.example.horapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Splash extends AppCompatActivity {
    private TextView appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("X onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.empowerSafe);
        Shader textShader = new LinearGradient(0, 0,appName.getPaint().measureText(appName.getText().toString()),appName.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        appName.getPaint().setShader(textShader);
        appName.setTextColor(Color.argb(255,0,0,0));



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                finish(); // Finish the current/splash screen activity
            }
        }, 2000); // 2000 milliseconds (2 seconds)
    }
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("X onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("X onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("X onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("X onPause()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("X onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("X onRestart()");
    }
}