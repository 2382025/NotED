package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Masih pakai layout yang sama

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Aksi tombol Login
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        // Aksi tombol Sign Up
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class); // Sudah disesuaikan
            startActivity(intent);
            finish();
        });
    }
}
