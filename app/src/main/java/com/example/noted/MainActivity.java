package com.example.noted;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Langsung arahkan ke LoginActivity saat aplikasi dibuka
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish(); // Selesaikan MainActivity agar tidak bisa kembali ke sini dengan tombol back
    }
}
