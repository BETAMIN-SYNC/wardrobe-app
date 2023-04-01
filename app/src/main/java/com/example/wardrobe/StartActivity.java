package com.example.wardrobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button loginNowBtn, registerNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerNowBtn = findViewById(R.id.registerBtn);
        loginNowBtn = findViewById(R.id.loginNowBtn);

        // opens register tab
        registerNowBtn.setOnClickListener(v -> startActivity(new Intent(StartActivity.this, RegisterActivity.class)));
        // opens login tab
        loginNowBtn.setOnClickListener(v -> startActivity(new Intent(StartActivity.this, LoginActivity.class)));
    }
}