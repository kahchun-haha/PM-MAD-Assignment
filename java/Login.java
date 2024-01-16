package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;

    private TextView forgetPassword;
    private ImageView login;
    private ImageView register;

    private ImageView hide;
    private ImageView unhide;

    private DatabaseReference mDatabase;


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


        ArrayList<String> emailList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userName : snapshot.getChildren()){
                    if(!userName.getKey().equals("post") && !userName.getKey().equals("notification"))
                     emailList.add(userName.child("Email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        forgetPassword = findViewById(R.id.forgotPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View dialogLayout = LayoutInflater.from(Login.this).inflate(R.layout.forget_password,null,false);
                builder.setView(dialogLayout);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                EditText givenEmail = dialogLayout.findViewById(R.id.realEmail);

                Button confirm = dialogLayout.findViewById(R.id.sureForgetBtn);
                Button cancel = dialogLayout.findViewById(R.id.undoForgetBtn);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean gotEmail = false;
                        if(givenEmail.getText().toString().isEmpty()){
                            Toast.makeText(Login.this, "Enter your registered email address",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            for(String email:emailList){
                                if(givenEmail.getText().toString().equals(email))
                                    gotEmail = true;
                            }

                            if(gotEmail){
                                mAuth.sendPasswordResetEmail(givenEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(Login.this, "Check your email",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else
                                            Toast.makeText(Login.this, "Failed to send",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                            else{
                                Toast.makeText(Login.this, "Enter your registered email address",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });


        hide = findViewById(R.id.hide);
        unhide = findViewById(R.id.unhide);



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

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide.setVisibility(View.GONE);
                unhide.setVisibility(View.VISIBLE);
                password.setTransformationMethod(null);
                password.setSelection(password.getText().toString().length());
            }
        });

        unhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unhide.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
                password.setTransformationMethod(new PasswordTransformationMethod());
                password.setSelection(password.getText().toString().length());

            }
        });

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
                if(username.getText().toString().isEmpty()){
                    username.setError("Enter registered email");
                    username.requestFocus();
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    password.setError("Enter password");
                    password.requestFocus();
                    return;
                }

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
                login.setImageAlpha(255);
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
