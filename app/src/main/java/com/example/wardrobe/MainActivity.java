package com.example.wardrobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button registerNowBtn = findViewById(R.id.registerNowBtn);
        final Button loginNowBtn = findViewById(R.id.loginNowBtn);

        //opens register tab
        registerNowBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Register.class)) );
        //opens login tab
        loginNowBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Login.class)) );
    }
}