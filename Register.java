
package com.example.horapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    EditText username;
    EditText email;
    EditText password;
    Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private CheckDuplicateName duplicateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        System.out.println("R onCreate()");

        duplicateName = new CheckDuplicateName(false);


        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_button);

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
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            username.setError("This field cannot be blank");
            username.requestFocus();
            return false;
           // Toast.makeText(this,"Please enter username",Toast.LENGTH_SHORT).show();
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
      // DatabaseReference userIDRef = mDatabase.child("UserID");
       // System.out.println(mDatabase.child("UserID").getParent());

     //   mDatabase.child(name).child("Email").setValue(email);
      mDatabase.child(name).child("Email").setValue(email);
   //     System.out.println(mDatabase.child("Dog").push().getKey());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //for(DataSnapshot smallShots : snapshot.getChildren())
                  // System.out.println(smallShots.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

@IgnoreExtraProperties
class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}