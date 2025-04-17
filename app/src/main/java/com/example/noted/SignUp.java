package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button signUpButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);

        // Aksi tombol Sign Up
        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login.class));
                finish();
            }
        });

        // Aksi tombol Log In
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}
