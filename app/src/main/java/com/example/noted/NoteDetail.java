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
            folder_id = "";
        }

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        doneButton = findViewById(R.id.doneButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);



        if (id == null || id.isEmpty()) {
            Date currentDate = new Date();
            createdAtDate = apiDateFormat.format(currentDate);
            createdAtTextView.setText(displayDateFormat.format(currentDate));
        } else {
            getNotes();
        }

        doneButton.setOnClickListener(v -> updateNotes());
        deleteButton.setOnClickListener(v -> showDeleteDialog());
        backButton.setOnClickListener(v -> goBackToHome());
    }



    private void getNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        Log.d("NoteDetail", "Fetching note with ID: " + id + " for user: " + userId);

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
                Log.d("NoteDetail", "Server response: " + s);
                showNote(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                // Menggunakan format query string yang sesuai dengan API
                // Format: showNoteDetail.php?user_id=X&id=Y
                String url = konfigurasi.URL_GET_NOTE_DETAIL + "user_id=" + userId + "&id=" + id;
                Log.d("NoteDetail", "Request URL: " + url);

                return rh.sendGetRequest(url);
            }
        }
        new GetNotes().execute();
    }

    private void showNote(String json) {
        try {
            Log.d("NoteDetail", "Raw JSON response: " + json);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.optJSONArray(konfigurasi.TAG_JSON_ARRAY);

            if (result == null || result.length() == 0) {
                Log.e("NoteDetail", "JSON Array is null or empty");
                Toast.makeText(this, "No note details found", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject c = result.getJSONObject(0);

            String title = c.optString(konfigurasi.TAG_TITLE, "Untitled");
            String created_at = c.optString(konfigurasi.TAG_CREATED_AT, "");
            String content = c.optString(konfigurasi.TAG_CONTENT, "");

            titleEditText.setText(title);
            contentEditText.setText(content);

            try {
                Date date = apiDateFormat.parse(created_at);
                createdAtTextView.setText(date != null ? displayDateFormat.format(date) : created_at);
            } catch (ParseException e) {
                Log.e("NoteDetail", "Date parsing error: " + e.getMessage());
                createdAtTextView.setText(created_at);
            }

        } catch (JSONException e) {
            Log.e("NoteDetail", "JSON parsing error: " + e.getMessage());
            Toast.makeText(this, "Error parsing note details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNotes() {
        final String title = titleEditText.getText().toString().trim();
        final String content = contentEditText.getText().toString().trim();

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        final String userId = sharedPreferences.getString("user_id", "");

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
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(NoteDetail.this, message, Toast.LENGTH_LONG).show();

                    if (status.equals("success")) {
                        goBackToHome();
                    }
                } catch (JSONException e) {
                    Toast.makeText(NoteDetail.this, "Error: " + s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(konfigurasi.KEY_NOTE_ID, id);
                hashMap.put(konfigurasi.KEY_NOTE_TITLE, title);
                hashMap.put(konfigurasi.KEY_NOTE_CONTENT, content);
                hashMap.put(konfigurasi.KEY_NOTE_FOLDER_ID, folder_id);
                hashMap.put(konfigurasi.KEY_USER_ID, userId); // Add user_id

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
        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        final String userId = sharedPreferences.getString("user_id", "");

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
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(NoteDetail.this, message, Toast.LENGTH_LONG).show();

                    if (status.equals("success")) {
                        goBackToHome();
                    }
                } catch (JSONException e) {
                    Toast.makeText(NoteDetail.this, "Error: " + s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("id", id);
                paramsMap.put("user_id", userId);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_DELETE_NOTE, paramsMap);
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