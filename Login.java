package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
 
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;
    Button login;
    Button register;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // In any activity just pass the context and use the singleton method
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(Login.this, Dashboard.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            System.out.println("??");
        }

        System.out.println("L onCreate()");

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_button);
       register = findViewById(R.id.login_register_btn);
        mAuth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
     //           dbHelper.addEntry();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = String.valueOf(username.getText());
                String userPass = String.valueOf(password.getText());
                mAuth.signInWithEmailAndPassword(userEmail,userPass)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Sign in successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this,Dashboard.class);
                                    startActivity(intent);
                                } else {
                                    System.out.println(task.getException().getMessage());
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("L onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("L onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("L onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("L onPause()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("L onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("L onRestart()");
    }
}
