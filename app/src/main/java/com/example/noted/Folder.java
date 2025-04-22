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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Folder extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    private ArrayList<HashMap<String, String>> folderList = new ArrayList<>();
    private SimpleAdapter adapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    ImageView profileIcon;
    Button tabAll, tabFolder;
    EditText searchEditText;
    ImageButton addFolderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        profileIcon = findViewById(R.id.profileIcon);
        tabAll = findViewById(R.id.tabAll);
        tabFolder = findViewById(R.id.tabFolder);
        searchEditText = findViewById(R.id.searchEditText);
        addFolderButton = findViewById(R.id.addFolderButton);

        addFolderButton.setOnClickListener(v -> {
            NewFolderDialog dialog = new NewFolderDialog();
            dialog.setOnFolderAddedListener(() -> getJSON()); // Refresh setelah tambah folder
            dialog.show(getSupportFragmentManager(), "NewFolderDialog");
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Folder.this, Profile.class);
            startActivity(intent);
        });

        tabAll.setOnClickListener(v -> {
            Intent intent = new Intent(Folder.this, Home.class);
            startActivity(intent);
        });

        setupSearchBar();
        getJSON(); // Load awal
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
                handler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showFolders() {
        JSONObject jsonObject;

        folderList.clear();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(konfigurasi.KEY_FOLDER_ID);   // Ambil "folder_id"
                String name = jo.getString(konfigurasi.KEY_FOLDER_NAME); // Ambil "name"

                HashMap<String, String> folder = new HashMap<>();
                folder.put(konfigurasi.KEY_FOLDER_ID, id);
                folder.put(konfigurasi.KEY_FOLDER_NAME, name);
                folderList.add(folder);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(
                Folder.this, folderList, R.layout.list_folder,
                new String[]{konfigurasi.KEY_FOLDER_ID, konfigurasi.KEY_FOLDER_NAME},
                new int[]{R.id.id, R.id.name}
        );

        listView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            adapter = new SimpleAdapter(
                    Folder.this, folderList, R.layout.list_folder,
                    new String[]{konfigurasi.KEY_FOLDER_ID, konfigurasi.KEY_FOLDER_NAME},
                    new int[]{R.id.id, R.id.name}
            );
            listView.setAdapter(adapter);
            return;
        }

        for (HashMap<String, String> item : folderList) {
            if (item.get(konfigurasi.KEY_FOLDER_NAME).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        SimpleAdapter newAdapter = new SimpleAdapter(
                Folder.this, filteredList, R.layout.list_folder,
                new String[]{konfigurasi.KEY_FOLDER_ID, konfigurasi.KEY_FOLDER_NAME},
                new int[]{R.id.id, R.id.name}
        );

        listView.setAdapter(newAdapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Folder.this, "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showFolders();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequest(konfigurasi.URL_GET_ALL_FOLDERS);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String folderId = map.get(konfigurasi.KEY_FOLDER_ID);
        String folderName = map.get(konfigurasi.KEY_FOLDER_NAME);

        Intent intent = new Intent(this, FolderDetail.class);
        intent.putExtra(konfigurasi.KEY_FOLDER_ID, folderId);
        intent.putExtra(konfigurasi.KEY_FOLDER_NAME, folderName);
        startActivity(intent);
    }
}
