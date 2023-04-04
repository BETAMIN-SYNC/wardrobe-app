package com.example.wardrobe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginNowBtn;
    TextView registerBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginNowBtn = findViewById(R.id.loginNowBtn);
        registerBtn = findViewById(R.id.registerBtn);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        loginNowBtn.setOnClickListener(v -> {
            ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please wait..");
            pd.show();

            String str_email = email.getText().toString();
            String str_password = password.getText().toString();

            if (TextUtils.isEmpty(str_email) && TextUtils.isEmpty(str_password)) {
                Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(LoginActivity.this, "Email is required.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(str_password)) {
                Toast.makeText(LoginActivity.this, "Password is required.", Toast.LENGTH_SHORT).show();
            } else if (str_password.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password must have 6 characters.", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(str_email, str_password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}