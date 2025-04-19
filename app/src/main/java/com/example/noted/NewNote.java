package com.example.noted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class NewNote extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private ImageButton backButton;

    private boolean noteSaved = false; // Untuk cek apakah sudah disimpan
    private String folderId; // <-- Ini sudah benar: pakai camelCase sesuai Java convention

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        backButton = findViewById(R.id.backButton);

        // Ambil folderId dari Intent
        Intent intent = getIntent();
        folderId = intent.getStringExtra(konfigurasi.FOLDER_ID);

        // Kalau tidak ada folderId, kasih default "" supaya aman
        if (folderId == null) {
            folderId = "";
        }

        backButton.setOnClickListener(v -> saveNoteAndGoHome());
    }

    private void saveNoteAndGoHome() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show();
            finish(); // Tetap kembali walaupun kosong
            return;
        }

        if (!noteSaved) { // Hanya simpan sekali
            addNote(title, content);
        }
    }

    private void addNote(String title, String content) {
        class AddNote extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NewNote.this, "Adding...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(NewNote.this, s, Toast.LENGTH_LONG).show();
                noteSaved = true; // Tandai sudah tersimpan
                startActivity(new Intent(NewNote.this, Home.class));
                finish();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put(konfigurasi.KEY_NOTE_TITLE, title);
                params.put(konfigurasi.KEY_NOTE_CONTENT, content);
                params.put(konfigurasi.KEY_NOTE_FOLDER_ID, folderId); // <-- ini sekarang bener, pakai KEY_NOTE_FOLDER_ID

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_ADD_NOTE, params);
            }
        }

        AddNote an = new AddNote();
        an.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kalau user tekan tombol back HP, tetap simpan dulu
        saveNoteAndGoHome();
    }
}
