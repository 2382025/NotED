package com.example.noted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Handler;

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

        // Inisialisasi view
        profileIcon = findViewById(R.id.profileIcon);
        tabAll = findViewById(R.id.tabAll);
        tabFolder = findViewById(R.id.tabFolder);
        searchEditText = findViewById(R.id.searchEditText);
        addFolderButton = findViewById(R.id.addFolderButton);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        addFolderButton.setOnClickListener(v -> {
            Intent intent = new Intent(Folder.this, NewFolder.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Folder.this, Profile.class);
            startActivity(intent);
        });

        // tabAll mengarah ke Home (Note)
        tabAll.setOnClickListener(v -> {
            Intent intent = new Intent(Folder.this, Home.class);
            startActivity(intent);
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

    private void showFolders() {
        JSONObject jsonObject;

        folderList.clear();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(konfigurasi.TAG_ID);
                String name = jo.getString(konfigurasi.TAG_FOLDER_NAME);

                HashMap<String, String> folder = new HashMap<>();
                folder.put(konfigurasi.TAG_ID, id);
                folder.put(konfigurasi.TAG_FOLDER_NAME, name);
                folderList.add(folder);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(
                Folder.this, folderList, R.layout.list_folder,
                new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_FOLDER_NAME},
                new int[]{R.id.id, R.id.name}
        );

        listView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            adapter = new SimpleAdapter(
                    Folder.this, folderList, R.layout.list_folder,
                    new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_FOLDER_NAME},
                    new int[]{R.id.id, R.id.name}
            );
            listView.setAdapter(adapter);
            return;
        }

        for (HashMap<String, String> item : folderList) {
            if (item.get(konfigurasi.TAG_FOLDER_NAME).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        SimpleAdapter newAdapter = new SimpleAdapter(
                Folder.this, filteredList, R.layout.list_folder,
                new String[]{konfigurasi.TAG_ID, konfigurasi.TAG_FOLDER_NAME},
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
        Intent intent = new Intent(this, FolderDetail.class);
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String folderId = map.get(konfigurasi.TAG_ID);
        intent.putExtra(konfigurasi.FOLDER_ID_EXTRA, folderId);
        startActivity(intent);
    }
}
