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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Home extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    private ArrayList<HashMap<String, String>> noteList = new ArrayList<>();
    private SimpleAdapter adapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    ImageButton addNoteButton;
    ImageView profileIcon;
    Button tabFolder;
    EditText searchEditText; // Tambahan searchEditText global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        addNoteButton = findViewById(R.id.addNoteButton);
        profileIcon = findViewById(R.id.profileIcon);
        tabFolder = findViewById(R.id.tabFolder);
        searchEditText = findViewById(R.id.searchEditText);

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, NewNote.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Profile.class);
            startActivity(intent);
        });

        tabFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Folder.class);
            startActivity(intent);
        });

        setupSearchBar(); // Pasang search bar
        getJSON(); // Ambil data
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Cancel delay sebelumnya
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                // Bikin delay 500ms baru filter
                searchRunnable = () -> filter(charSequence.toString());
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void showNotes() {
        JSONObject jsonObject;

        noteList.clear(); // Biar data ga dobel
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(konfigurasi.TAG_ID);
                String title = jo.getString(konfigurasi.TAG_TITLE);

                HashMap<String, String> note = new HashMap<>();
                note.put(konfigurasi.TAG_ID, id);
                note.put(konfigurasi.TAG_TITLE, title);
                noteList.add(note);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(
                Home.this, noteList, R.layout.list_note,
                new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_TITLE},
                new int[]{R.id.id, R.id.title}
        );

        listView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            // Kalau search kosong, tampilkan semua notes
            adapter = new SimpleAdapter(
                    Home.this, noteList, R.layout.list_note,
                    new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_TITLE},
                    new int[]{R.id.id, R.id.title}
            );
            listView.setAdapter(adapter);
            return;
        }

        // Kalau ada isinya, filter berdasarkan title
        for (HashMap<String, String> item : noteList) {
            if (item.get(konfigurasi.TAG_TITLE).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        SimpleAdapter newAdapter = new SimpleAdapter(
                Home.this, filteredList, R.layout.list_note,
                new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_TITLE},
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
                loading = ProgressDialog.show(Home.this, "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showNotes();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(konfigurasi.URL_GET_ALL_NOTES);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteDetail.class);
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String noteId = map.get(konfigurasi.TAG_ID); // Ambil ID
        intent.putExtra(konfigurasi.NOTE_ID, noteId);
        startActivity(intent);
    }
}
