package com.example.noted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NoteDetail extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private TextView createdAtTextView;
    private String id;
    private String folder_id;
    private Button doneButton;
    private ImageButton backButton, deleteButton;

    private String createdAtDate = ""; // Untuk menyimpan tanggal dan waktu saat buat catatan baru
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);


        Intent intent = getIntent();
        id = intent.getStringExtra(konfigurasi.NOTE_ID);
        folder_id = intent.getStringExtra(konfigurasi.FOLDER_ID);
        if (folder_id == null) {
            folder_id = "0";
        }

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        doneButton = findViewById(R.id.doneButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        TextView folderIdTextView = findViewById(R.id.folderIdTextView);
        if (folderIdTextView != null) {
            folderIdTextView.setText("Folder ID: " + folder_id);
        }

        // Generate tanggal dan waktu otomatis untuk catatan baru
        if (id == null || id.isEmpty()) {
            Date currentDate = new Date();
            createdAtDate = apiDateFormat.format(currentDate); // Format untuk dikirim ke API
            createdAtTextView.setText(displayDateFormat.format(currentDate)); // Format untuk ditampilkan
        } else {
            getNotes();
        }

        doneButton.setOnClickListener(v -> updateNotes());
        deleteButton.setOnClickListener(v -> showDeleteDialog());
        backButton.setOnClickListener(v -> goBackToHome());
    }

    private void getNotes() {
        class GetNotes extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NoteDetail.this, "Mengambil...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showNote(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String userId = getSharedPreferences("UserSession", MODE_PRIVATE).getString("user_id", "0");

                HashMap<String, String> dataParams = new HashMap<>();
                dataParams.put("id", id);
                dataParams.put("user_id", userId);

                return rh.sendGetRequest(konfigurasi.URL_GET_NOTE_DETAIL, dataParams);


            }
        }
        new GetNotes().execute();
    }

    private void showNote(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            String title = c.getString(konfigurasi.TAG_TITLE);
            String created_at = c.getString(konfigurasi.TAG_CREATED_AT);
            String content = c.getString(konfigurasi.TAG_CONTENT);


            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            String userId = prefs.getString("user_id", "0");
            Log.d("NoteDetail", "User ID from SharedPreferences: " + userId);


            titleEditText.setText(title);
            contentEditText.setText(content);

            // Format tanggal dari API ke tampilan
            try {
                Date date = apiDateFormat.parse(created_at);
                if (date != null) {
                    createdAtDate = created_at; // Simpan format asli untuk dikirim kembali ke API
                    createdAtTextView.setText(displayDateFormat.format(date));
                } else {
                    createdAtTextView.setText(created_at);
                }
            } catch (ParseException e) {
                createdAtTextView.setText(created_at);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateNotes() {
        final String title = titleEditText.getText().toString().trim();
        final String content = contentEditText.getText().toString().trim();

        // Gunakan createdAtDate yang disimpan dalam format API
        final String createdAtForApi = (id == null || id.isEmpty()) ? createdAtDate : createdAtDate;

        class UpdateNotes extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NoteDetail.this, "Menyimpan...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(NoteDetail.this, s, Toast.LENGTH_LONG).show();
                goBackToHome();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(konfigurasi.KEY_NOTE_ID, id);
                hashMap.put(konfigurasi.KEY_NOTE_TITLE, title);
                hashMap.put(konfigurasi.KEY_NOTE_CONTENT, content);
                hashMap.put(konfigurasi.KEY_NOTE_FOLDER_ID, folder_id);
                hashMap.put(konfigurasi.KEY_NOTE_CREATED_AT, createdAtForApi); // Kirim format yang sesuai untuk API

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_UPDATE_NOTE, hashMap);
            }
        }
        new UpdateNotes().execute();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View deleteDialogView = getLayoutInflater().inflate(R.layout.delete_note_dialog, null);
        builder.setView(deleteDialogView);

        Button cancelButton = deleteDialogView.findViewById(R.id.cancelButton);
        Button okButton = deleteDialogView.findViewById(R.id.okButton);

        AlertDialog deleteDialog = builder.create();

        cancelButton.setOnClickListener(v -> deleteDialog.dismiss());
        okButton.setOnClickListener(v -> {
            deleteNotes();
            deleteDialog.dismiss();
        });

        deleteDialog.show();
    }

    private void deleteNotes() {
        class DeleteNotes extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NoteDetail.this, "Menghapus...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(NoteDetail.this, s, Toast.LENGTH_LONG).show();
                if (s.toLowerCase().contains("success")) {
                    goBackToHome();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(konfigurasi.URL_DELETE_NOTE, id);
            }
        }
        new DeleteNotes().execute();
    }

    private void goBackToHome() {
        Intent intent = new Intent(NoteDetail.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(konfigurasi.FOLDER_ID, folder_id);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToHome();
    }
}