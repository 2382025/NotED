package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    TextView nameTextView, emailTextView;
    Button changePasswordButton, logoutButton;
    ImageView backButton; // Ganti dari backIcon ke backButton agar sesuai ID dan deklarasi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi view
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        backButton = findViewById(R.id.backButton); // Sesuai dengan ID di XML

        // Ambil data dari intent (atau bisa juga SharedPreferences)
        String name = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");

        nameTextView.setText(name != null ? name : "Your Name");
        emailTextView.setText(email != null ? email : "you@example.com");

        // Tombol back
        backButton.setOnClickListener(v -> finish());

        // Tombol Change Password
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, ChangePassword.class);
            startActivity(intent);
        });

        // Tombol Log Out
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish(); // Selesai dari halaman ini
        });
    }
}
