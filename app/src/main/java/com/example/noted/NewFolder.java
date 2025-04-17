package com.example.noted;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class NewFolder extends AppCompatActivity {

    private EditText folderNameEditText;
    private Button okButton, cancelButton;
    private boolean noteSaved = false; // Untuk cek apakah sudah disimpan atau belum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_folder_dialog);

        folderNameEditText = findViewById(R.id.folderNameEditText);
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = folderNameEditText.getText().toString().trim();
                if (!folderName.isEmpty()) {
                    addFolder(folderName);
                } else {
                    Toast.makeText(NewFolder.this, "Nama folder tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Tutup dialog kalau cancel
            }
        });
    }

    // Method untuk menambahkan folder ke database
    private void addFolder(String folder) {
        class AddFolder extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NewFolder.this, "Adding...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(NewFolder.this, s, Toast.LENGTH_LONG).show();
                noteSaved = true;
                // Setelah simpan, balik ke daftar folder
                startActivity(new Intent(NewFolder.this, Folder.class));
                finish();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put(konfigurasi.KEY_FOLDER_NAME, folder);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_ADD_FOLDER, params);
            }
        }

        AddFolder af = new AddFolder();
        af.execute();
    }
}
