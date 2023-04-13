package com.example.wardrobe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText fullname, email, username, password;
    Button registerBtn;
    TextView loginNowBtn;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        registerBtn = findViewById(R.id.registerBtn);
        loginNowBtn = findViewById(R.id.loginNowBtn);

        auth = FirebaseAuth.getInstance();

        loginNowBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        registerBtn.setOnClickListener(v -> {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Please wait..");
            pd.show();

            String str_fullname = fullname.getText().toString();
            String str_email = email.getText().toString();
            String str_username = username.getText().toString();
            String str_password = password.getText().toString();

            if (TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_username)
                    || TextUtils.isEmpty(str_password)) {
                Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else if (str_password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must have 6 characters.", Toast.LENGTH_SHORT).show();
            } else {
                registerBtn(str_fullname, str_email, str_username, str_password);
            }
        });
    }

    private void registerBtn(String fullname, String email, String username, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username.toLowerCase());
                        hashMap.put("fullname", fullname);
                        hashMap.put("bio", "");
                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/wardrobe-6b1b9.appspot.com/o/user.png?alt=media&token=d7d7dca1-7f70-47c6-b50c-7668a603be11");

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,"You can't register with this email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}