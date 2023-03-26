package com.example.wardrobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button loginNowBtn, registerNowBtn;

    FirebaseUser firebaseUser;

    //@Override
    //protected void onStart() {
       //super.onStart();

        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // redirect if user is not null
        //if (firebaseUser != null) {
            //startActivity(new Intent(StartActivity.this, MainActivity.class));
            //finish();
        //}
    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerNowBtn = findViewById(R.id.registerBtn);
        loginNowBtn = findViewById(R.id.loginNowBtn);

        // opens register tab
        registerNowBtn.setOnClickListener(v -> startActivity(new Intent(StartActivity.this, Register.class)));
        // opens login tab
        loginNowBtn.setOnClickListener(v -> startActivity(new Intent(StartActivity.this, Login.class)));
    }
}