package com.example.noted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, konfigurasi.URL_LOGIN,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            String userId = jsonResponse.getString("user_id");
                            String name = jsonResponse.getString("name");
                            String userEmail = jsonResponse.getString("email");

                            // Simpan data ke SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id", userId);
                            editor.putString("name", name);
                            editor.putString("email", userEmail);
                            editor.apply();

                            Log.d("LOGIN_SESSION", "user_id: " + userId);


                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonResponse.optString("message", "Login Failed");
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(Login.this, "Response Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Login.this, "Login Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
