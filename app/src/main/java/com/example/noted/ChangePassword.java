package com.example.noted;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePassword extends AppCompatActivity {

    EditText currentPassword, newPassword, confirmPassword;
    Button continueButton;
    ImageView cancelBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        continueButton = findViewById(R.id.continueButton);
        cancelBack = findViewById(R.id.cancelBack);

        cancelBack.setOnClickListener(v -> {
            finish(); // balik ke profile
        });

        continueButton.setOnClickListener(v -> {
            // Validasi password di sini
        });
    }
}
