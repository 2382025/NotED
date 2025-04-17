package com.example.noted;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class NewNote extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private ImageButton backButton;

    private boolean noteSaved = false; // Untuk cek apakah sudah disimpan atau belum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        backButton = findViewById(R.id.backButton);

        // Tombol kembali
        backButton.setOnClickListener(v -> saveNoteAndGoHome());
    }

    // Method untuk simpan note lalu kembali ke Home
    private void saveNoteAndGoHome() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show();
            finish(); // Tetap kembali ke Home walau kosong
            return;
        }

        if (!noteSaved) { // Hanya simpan sekali
            addNote(title, content);
        }
    }

    // Method untuk menambahkan note ke database
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
                // Setelah simpan, langsung ke Home
                startActivity(new Intent(NewNote.this, Home.class));
                finish();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put(konfigurasi.KEY_NOTE_TITLE, title);
                params.put(konfigurasi.KEY_NOTE_CONTENT, content);

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
