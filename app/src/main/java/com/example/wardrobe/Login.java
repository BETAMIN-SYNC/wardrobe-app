package com.example.wardrobe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //accesses firebase's Realtime Database for the accounts
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wardrobe-6b1b9-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText userName = findViewById(R.id.userName);
        final EditText password = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.loginBtn);
        ImageView googleBtn = findViewById(R.id.googleBtn);

        loginBtn.setOnClickListener(v -> {
            final String userNameTxt = userName.getText().toString();
            final String passwordTxt = password.getText().toString();

            if (userNameTxt.isEmpty() || passwordTxt.isEmpty()){
                Toast.makeText(Login.this, "Enter your username and password to continue", Toast.LENGTH_SHORT).show();
            }
            else{

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //checks if the username exists in the database
                        if (snapshot.hasChild(userNameTxt)){

                            final String getPassword = snapshot.child(userNameTxt).child("password").getValue(String.class);

                            assert getPassword != null;
                            if (getPassword.equals(passwordTxt)){
                                Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();

                                //opens the main tab upon success
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(Login.this, "Incorrect Username", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}