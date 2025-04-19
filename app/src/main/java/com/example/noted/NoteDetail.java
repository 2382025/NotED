package com.example.noted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private String folder_id; // Added to track the folder_id
    private Button doneButton;
    private ImageButton backButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        // Ambil id dari intent
        Intent intent = getIntent();
        id = intent.getStringExtra(konfigurasi.NOTE_ID);

        // Retrieve folder_id from intent if available, otherwise set to default
        folder_id = intent.getStringExtra(konfigurasi.FOLDER_ID);
        if (folder_id == null) {
            folder_id = "0"; // Default folder_id if not provided
        }

        // Hubungkan dengan layout
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        doneButton = findViewById(R.id.doneButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        // Display folder ID if the TextView exists in layout
        TextView folderIdTextView = findViewById(R.id.folderIdTextView);
        if (folderIdTextView != null) {
            folderIdTextView.setText("Folder ID: " + folder_id);
        }

        // Ambil data note
        getNotes();

        // Set klik listener
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
                return rh.sendGetRequestParam(konfigurasi.URL_GET_NOTE_DETAIL, id);
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

            // Get folder_id from JSON if available
            if (c.has(konfigurasi.TAG_FOLDER_ID)) {
                folder_id = c.getString(konfigurasi.TAG_FOLDER_ID);

                // Update the TextView if it exists
                TextView folderIdTextView = findViewById(R.id.folderIdTextView);
                if (folderIdTextView != null) {
                    folderIdTextView.setText("Folder ID: " + folder_id);
                }
            }

            titleEditText.setText(title);
            contentEditText.setText(content);

            // Format tanggal
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

            try {
                Date date = inputFormat.parse(created_at);
                if (date != null) {
                    createdAtTextView.setText(outputFormat.format(date));
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
                goBackToHome(); // Balik ke home setelah update
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(konfigurasi.KEY_NOTE_ID, id);
                hashMap.put(konfigurasi.KEY_NOTE_TITLE, title);
                hashMap.put(konfigurasi.KEY_NOTE_CONTENT, content);
                hashMap.put(konfigurasi.KEY_NOTE_FOLDER_ID, folder_id); // Include folder_id in update

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
        // Pass folder_id back to home if needed
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