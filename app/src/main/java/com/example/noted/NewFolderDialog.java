package com.example.noted;

import android.app.Dialog;
import android.app.ProgressDialog;
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

    // Interface untuk callback ke Activity
    public interface OnFolderAddedListener {
        void onFolderAdded();
    }

    // Setter untuk listener
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

        okButton.setOnClickListener(v -> {
            String folderName = folderNameEditText.getText().toString().trim();
            if (!folderName.isEmpty()) {
                addFolder(folderName);
            } else {
                Toast.makeText(getActivity(), "Folder Name Can't Be Empty", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    // Method untuk menambahkan folder ke database
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
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

                if (listener != null) {
                    listener.onFolderAdded(); // kasih tahu Activity kalau sudah berhasil
                }

                dismiss(); // tutup dialog
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put(konfigurasi.KEY_FOLDER_NAME, folderName);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(konfigurasi.URL_ADD_FOLDER, params);
            }
        }

        AddFolder af = new AddFolder();
        af.execute();
    }
}
