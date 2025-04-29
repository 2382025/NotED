package com.example.noted;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FolderDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private EditText searchEditText;
    private ImageButton addNoteButton;
    private EditText folderNameEditText;
    private Button doneButton;
    private ImageButton deleteButton;

    private String folder_id;
    private String folder_name;
    private String JSON_STRING;

    private ArrayList<HashMap<String, String>> noteList = new ArrayList<>();
    private SimpleAdapter adapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);

        // Initialize views
        listView = findViewById(R.id.listView);
        searchEditText = findViewById(R.id.searchEditText);
        addNoteButton = findViewById(R.id.addNoteButton);
        folderNameEditText = findViewById(R.id.folderNameEditText);
        doneButton = findViewById(R.id.doneButton);
        deleteButton = findViewById(R.id.deleteButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set click listeners
        listView.setOnItemClickListener(this);

        backButton.setOnClickListener(v -> goBackToFolder());
        doneButton.setOnClickListener(v -> updateFolder());
        deleteButton.setOnClickListener(v -> showDeleteDialog());
        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(FolderDetail.this, NewNote.class);
            intent.putExtra(konfigurasi.KEY_FOLDER_ID, folder_id);
            startActivity(intent);
        });

        // Get folder data from intent
        Intent intent = getIntent();
        folder_id = intent.getStringExtra(konfigurasi.KEY_FOLDER_ID);
        folder_name = intent.getStringExtra(konfigurasi.KEY_FOLDER_NAME);

        if (folder_id == null || folder_id.isEmpty()) {
            Toast.makeText(this, "Folder ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        folderNameEditText.setText(folder_name);
        setupSearchBar();
        getJSON();
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> filterNotes(s.toString());
                handler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showNotes() {
        noteList.clear();
        try {
            JSONObject jsonObject = new JSONObject(JSON_STRING);

            if (jsonObject.getString("status").equals("success")) {
                JSONArray result = jsonObject.getJSONArray("result");

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String id = jo.getString(konfigurasi.KEY_NOTE_ID);
                    String title = jo.getString(konfigurasi.KEY_NOTE_TITLE);

                    HashMap<String, String> note = new HashMap<>();
                    note.put(konfigurasi.KEY_NOTE_ID, id);
                    note.put(konfigurasi.KEY_NOTE_TITLE, title);
                    noteList.add(note);
                }
            } else {
                String error = jsonObject.getString("message");
                Log.e("FolderDetail", "Server error: " + error);
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show();
        }

        if (noteList.isEmpty()) {
            HashMap<String, String> emptyNote = new HashMap<>();
            emptyNote.put(konfigurasi.KEY_NOTE_ID, "");
            emptyNote.put(konfigurasi.KEY_NOTE_TITLE, "Folder kosong");
            noteList.add(emptyNote);
            listView.setOnItemClickListener(null);
        } else {
            listView.setOnItemClickListener(this);
        }

        adapter = new SimpleAdapter(
                this, noteList, R.layout.list_note,
                new String[]{konfigurasi.KEY_NOTE_ID, konfigurasi.KEY_NOTE_TITLE},
                new int[]{R.id.id, R.id.title}
        );
        listView.setAdapter(adapter);
    }

    private void filterNotes(String query) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        for (HashMap<String, String> item : noteList) {
            if (item.get(konfigurasi.KEY_NOTE_TITLE).toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        SimpleAdapter filteredAdapter = new SimpleAdapter(
                this, filteredList, R.layout.list_note,
                new String[]{konfigurasi.KEY_NOTE_ID, konfigurasi.KEY_NOTE_TITLE},
                new int[]{R.id.id, R.id.title}
        );
        listView.setAdapter(filteredAdapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FolderDetail.this,
                        "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.d("FolderDetail", "Server Response: " + s);

                if (s == null || s.trim().isEmpty()) {
                    Toast.makeText(FolderDetail.this,
                            "Empty response from server", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSON_STRING = s;
                showNotes();
            }

            @Override
            protected String doInBackground(Void... params) {
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                String userId = prefs.getString("user_id", "");

                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("user_id", userId);
                paramsMap.put("folder_id", folder_id);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_GET_FOLDER_DETAIL, paramsMap);
            }
        }
        new GetJSON().execute();
    }

    private void updateFolder() {
        final String newName = folderNameEditText.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Nama folder tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        class UpdateFolder extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FolderDetail.this,
                        "Menyimpan...", "Tunggu sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(FolderDetail.this, message, Toast.LENGTH_LONG).show();

                    if (status.equals("success")) {
                        folder_name = newName;
                    }
                } catch (JSONException e) {
                    Toast.makeText(FolderDetail.this, "Error: " + s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                String userId = prefs.getString("user_id", "");

                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(konfigurasi.KEY_FOLDER_ID, folder_id);
                paramsMap.put(konfigurasi.KEY_FOLDER_NAME, newName);
                paramsMap.put(konfigurasi.KEY_USER_ID, userId);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_UPDATE_FOLDER, paramsMap);
            }
        }
        new UpdateFolder().execute();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.delete_folder_dialog, null);
        builder.setView(dialogView);

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button okButton = dialogView.findViewById(R.id.okButton);

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        okButton.setOnClickListener(v -> {
            deleteFolder();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteFolder() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        final String userId = sharedPreferences.getString("user_id", "");

        class DeleteFolder extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FolderDetail.this,
                        "Menghapus...", "Tunggu sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(FolderDetail.this, message, Toast.LENGTH_LONG).show();

                    if (status.equals("success")) {
                        goBackToFolder();
                    }
                } catch (JSONException e) {
                    Toast.makeText(FolderDetail.this, "Error: " + s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("folder_id", folder_id);
                paramsMap.put("user_id", userId);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_DELETE_FOLDER, paramsMap);
            }
        }
        new DeleteFolder().execute();
    }

    private void goBackToFolder() {
        Intent intent = new Intent(this, Folder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String noteId = map.get(konfigurasi.KEY_NOTE_ID);

        if (noteId == null || noteId.isEmpty()) return;

        Intent intent = new Intent(this, NoteDetail.class);
        intent.putExtra(konfigurasi.NOTE_ID, noteId);
        intent.putExtra(konfigurasi.KEY_FOLDER_ID, folder_id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToFolder();
    }
}