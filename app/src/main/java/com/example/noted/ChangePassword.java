package com.example.noted;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmPassword;
    private Button continueButton;
    private ImageView cancelBack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Inisialisasi view
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        continueButton = findViewById(R.id.continueButton);
        cancelBack = findViewById(R.id.cancelBack);

        // Dapatkan user ID dari SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        cancelBack.setOnClickListener(v -> finish());

        continueButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        // Validasi input
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan user ID dari SharedPreferences
        String userId = sharedPreferences.getString("user_id", "");
        if (userId.isEmpty()) {
            Toast.makeText(this, "Sesi tidak valid, silakan login kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proses perubahan password
        class ChangePasswordTask extends android.os.AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ChangePassword.this,
                        "Memproses...", "Sedang mengubah password...", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                // Buat parameter request
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("current_password", currentPass);
                params.put("new_password", newPass);

                RequestHandler handler = new RequestHandler();
                return handler.sendPostRequest(konfigurasi.URL_CHANGE_PASSWORD, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    Toast.makeText(ChangePassword.this, message, Toast.LENGTH_LONG).show();

                    if (status.equals("success")) {
                        // Jika berhasil, tutup activity
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangePassword.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }

        new ChangePasswordTask().execute();
    }
}