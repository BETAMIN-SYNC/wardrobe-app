package com.example.wardrobe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    //creates object of DatabaseReference class to access firebase's Realtime Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wardrobe-6b1b9-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fullName = findViewById(R.id.fullName);
        final EditText userName = findViewById(R.id.userName);
        final EditText password = findViewById(R.id.password);
        final EditText conPassword = findViewById(R.id.conPassword);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNowBtn);


        registerBtn.setOnClickListener(v -> {

            //get data from EditTexts into String variables
            final String fullNameTxt = fullName.getText().toString();
            final String userNameTxt = userName.getText().toString();
            final String passwordTxt = password.getText().toString();
            final String conPasswordTxt = conPassword.getText().toString();

            //checks if user fill all the fields before sending data to firebase
            if(fullNameTxt.isEmpty() || userNameTxt.isEmpty() || passwordTxt.isEmpty() || conPasswordTxt.isEmpty()){
                Toast.makeText(Register.this, "Fill in required fields", Toast.LENGTH_SHORT).show();
            }

            //checks if the corresponding password matches
            else if (!passwordTxt.equals(conPasswordTxt)){
                Toast.makeText(Register.this, "Your password and confirmation password do not match", Toast.LENGTH_SHORT).show();
            }
            else {
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //checks if username is not currently registered
                        if (snapshot.hasChild(userNameTxt)){
                            Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            //sends data to firebase Realtime Database
                            databaseReference.child("users").child(userNameTxt).child("fullName").setValue(fullNameTxt);
                            databaseReference.child("users").child(userNameTxt).child("password").setValue(passwordTxt);

                            //shows success message and returns to main activity
                            Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        });

        //opens login tab
        loginNowBtn.setOnClickListener(v -> startActivity(new Intent(Register.this, Login.class)) );


    }
}