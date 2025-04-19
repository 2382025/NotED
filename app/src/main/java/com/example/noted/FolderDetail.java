package com.example.noted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FolderDetail extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private EditText searchEditText;
    private ImageButton addNoteButton;
    private TextView folderNameTextView;
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

        listView = findViewById(R.id.listView);
        searchEditText = findViewById(R.id.searchEditText);
        addNoteButton = findViewById(R.id.addNoteButton);
        folderNameTextView = findViewById(R.id.folderNameTextView);

        listView.setOnItemClickListener(this);

        // Ambil data dari intent
        Intent intent = getIntent();
        folder_id = intent.getStringExtra(konfigurasi.KEY_FOLDER_ID);
        folder_name = intent.getStringExtra(konfigurasi.KEY_FOLDER_NAME);

        if (folder_id == null || folder_id.isEmpty()) {
            Toast.makeText(this, "Folder ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (folder_name != null) {
            folderNameTextView.setText(folder_name);
        }

        addNoteButton.setOnClickListener(v -> {
            Intent addNoteIntent = new Intent(FolderDetail.this, NewNote.class);
            addNoteIntent.putExtra(konfigurasi.KEY_FOLDER_ID, folder_id);
            startActivity(addNoteIntent);
        });

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
                searchRunnable = () -> filter(s.toString());
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showNotes() {
        JSONObject jsonObject;
        noteList.clear();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("id");
                String title = jo.getString("title");

                HashMap<String, String> note = new HashMap<>();
                note.put(konfigurasi.KEY_NOTE_ID, id);
                note.put(konfigurasi.KEY_NOTE_TITLE, title);
                noteList.add(note);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (noteList.isEmpty()) {
            HashMap<String, String> emptyNote = new HashMap<>();
            emptyNote.put(konfigurasi.KEY_NOTE_ID, "");
            emptyNote.put(konfigurasi.KEY_NOTE_TITLE, "This folder is empty.");
            noteList.add(emptyNote);
            listView.setOnItemClickListener(null);
        } else {
            listView.setOnItemClickListener(this);
        }

        adapter = new SimpleAdapter(
                FolderDetail.this, noteList, R.layout.list_note,
                new String[]{konfigurasi.KEY_NOTE_ID, konfigurasi.KEY_NOTE_TITLE},
                new int[]{R.id.id, R.id.title}
        );
        listView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
        for (HashMap<String, String> item : noteList) {
            if (item.get(konfigurasi.KEY_NOTE_TITLE).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        SimpleAdapter newAdapter = new SimpleAdapter(
                FolderDetail.this, filteredList, R.layout.list_note,
                new String[]{konfigurasi.KEY_NOTE_ID, konfigurasi.KEY_NOTE_TITLE},
                new int[]{R.id.id, R.id.title}
        );
        listView.setAdapter(newAdapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FolderDetail.this, "Mengambil Data", "Mohon tunggu...", false, false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showNotes();
            }
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();
                // Use the folder detail endpoint with the folder_id
                return rh.sendGetRequestParam(konfigurasi.URL_GET_FOLDER_DETAIL, folder_id);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String noteId = map.get(konfigurasi.KEY_NOTE_ID);

        if (noteId == null || noteId.isEmpty()) return;

        Intent intent = new Intent(FolderDetail.this, NoteDetail.class);
        intent.putExtra(konfigurasi.NOTE_ID, noteId);
        startActivity(intent);
    }
}
