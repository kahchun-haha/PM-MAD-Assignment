
package com.example.horapp;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;
    private ImageView register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ImageView back;

    private CheckDuplicateName duplicateName;

    private ImageView hide;
    private ImageView unhide;

    private TextView symbol;
    private TextView uppercase;
    private TextView lowercase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        System.out.println("R onCreate()");

        duplicateName = new CheckDuplicateName(false);


        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.back_in_register);
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_register_btn);

        hide = findViewById(R.id.register_hide);
        unhide = findViewById(R.id.register_unhide);

        symbol = findViewById(R.id.symbol);
        uppercase = findViewById(R.id.uppercase);
        lowercase = findViewById(R.id.lowercase);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();

                    boolean hasLowercase = password.matches(".*[a-z].*");
                    boolean hasUppercase = password.matches(".*[A-Z].*");
                    boolean hasSymbol = password.matches(".*[^a-zA-Z0-9].*");

                    lowercase.setTextColor(hasLowercase ? getResources().getColor(R.color.light_blue) : Color.parseColor("#808080"));
                    uppercase.setTextColor(hasUppercase ? getResources().getColor(R.color.light_blue) : Color.parseColor("#808080"));
                    symbol.setTextColor(hasSymbol ? getResources().getColor(R.color.light_blue) : Color.parseColor("#808080"));



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setImageAlpha(100);
                finish();
                Intent i = new Intent(Register.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!isNameEmpty(username))
                    repeatedUsername(username);
            }
        });




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    createAcc();
                }
            }

        });
    }


    public boolean validateUserName(){
        if(duplicateName.isDuplicate()){
            username.setError("Username has already been taken");
            username.requestFocus();
            System.out.println(username.getText().toString()+" "+duplicateName.isDuplicate());
            //sameName.setRepeat(false);
            return false;
        }
        else
            return true;
    }
    public void createAcc(){

        String userName = String.valueOf(username.getText());
        String userEmail = String.valueOf(email.getText());
        String userPass = String.valueOf(password.getText());
        mAuth.createUserWithEmailAndPassword(userEmail,userPass)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeNewUser(userName,userEmail);
                            Toast.makeText(Register.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this,Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            // either incorrect email format,
                            // same email as other users or password not 6 characters
                            Toast.makeText(Register.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public boolean validateData() {
        System.out.println("Do u run here?");

        if(isNameEmpty(username)){
            username.setError("Enter username");
            username.requestFocus();
            return false;
        }

        if(username.getText().toString().length()<3){
            username.setError("Username must be at least 3 characters");
            username.requestFocus();
            return  false;
        }

        if(!isEmail(email)){
            email.setError("Enter a valid email address");
            email.requestFocus();
            return false;
        }

        if(isPasswordEmpty(password)){
            password.setError("Password is required");
            password.requestFocus();
            return false;
        }

        if(!isPasswordEmpty(password)){
            if(password.getText().toString().length()>=6){
                if(!strongPassword(password)){
                    password.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNameEmpty(EditText text){
        CharSequence str = String.valueOf(text.getText());
        return TextUtils.isEmpty(str);
    }

    public boolean isEmail(EditText text){
        CharSequence str = String.valueOf(text.getText());
        return (!TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches());
    }

    public boolean isPasswordEmpty(EditText text){
        CharSequence str = String.valueOf(text.getText());
        return TextUtils.isEmpty(str);
    }



    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("R onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("R onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("R onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("R onPause()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("R onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("R onRestart()");
    }


    public void writeNewUser(String name, String email) {
        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");

        mDatabase.child(name).child("Email").setValue(email);

    }

    public void repeatedUsername(EditText text){

        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("What u doing here");
                duplicateName.setIsDuplicate(false);
                for(DataSnapshot userName : snapshot.getChildren()){
                    if(String.valueOf(text.getText()).equals(userName.getKey())) {
                        //System.out.println(userName.getKey());
                        duplicateName.setIsDuplicate(true);
                        validateUserName();
                        System.out.println(". "+duplicateName.isDuplicate());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public boolean strongPassword(EditText password){
        boolean hasSymbol = false;
        boolean hasUpper = false;
        boolean hasLower = false;

        String strPassword = password.getText().toString();
        for(char c : strPassword.toCharArray()){
            if(Character.isLowerCase(c))
                hasLower = true;
            if(Character.isUpperCase(c))
                hasUpper = true;
            if(!Character.isLetterOrDigit(c))
                hasSymbol = true;
        }

        if(!hasSymbol){
            password.setError("Password includes at least one symbol");
            return hasSymbol;
        }
        else if(!hasLower){
            password.setError("Password includes at least one lowercase alphabet");
            return hasLower;
        }
        else if(!hasUpper){
            password.setError("Password includes at least one uppercase alphabet");
            return hasUpper;
        }
        else{
            return  hasLower && hasSymbol && hasUpper;
        }
    }
}


class CheckDuplicateName{
    private boolean duplicate;
    public CheckDuplicateName(boolean duplicate){
        this.duplicate = duplicate;
    }

    public boolean isDuplicate(){
        return duplicate;
    }

    public void setIsDuplicate(boolean duplicate){
        this.duplicate = duplicate;
    }
}
