package com.example.noted;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

public class NewFolderDialog extends DialogFragment {

    private EditText folderNameEditText;
    private Button okButton, cancelButton;

    private OnFolderAddedListener listener;
    private String user_id;

    public interface OnFolderAddedListener {
        void onFolderAdded();
    }

    public void setOnFolderAddedListener(OnFolderAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_folder_dialog, null);

        folderNameEditText = view.findViewById(R.id.folderNameEditText);
        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        builder.setView(view);

        // Ambil user_id dari SharedPreferences seperti di NewNote.java
        SharedPreferences preferences = getActivity().getSharedPreferences("UserSession", 0);
        user_id = preferences.getString("user_id", "0");

        okButton.setOnClickListener(v -> {
            String folderName = folderNameEditText.getText().toString().trim();
            if (folderName.isEmpty()) {
                Toast.makeText(getActivity(), "Folder Name Can't Be Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (user_id.equals("0")) {
                Toast.makeText(getActivity(), "User ID is missing. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            addFolder(folderName);
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    private void addFolder(String folderName) {
        class AddFolder extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Adding...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s != null && s.toLowerCase().contains("success")) {
                    Toast.makeText(getActivity(), "Folder successfully added", Toast.LENGTH_LONG).show();
                    if (listener != null) {
                        listener.onFolderAdded();
                    }
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Failed to add folder: " + s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put(konfigurasi.KEY_USER_ID, user_id);
                params.put(konfigurasi.KEY_FOLDER_NAME, folderName);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_ADD_FOLDER, params);
            }
        }

        new AddFolder().execute();
    }
}
