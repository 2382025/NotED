package com.example.noted;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class FolderAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> folderList;
    private LayoutInflater inflater;

    public FolderAdapter(Context context, ArrayList<HashMap<String, String>> folderList) {
        this.context = context;
        this.folderList = folderList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView folderName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_folder, parent, false);
            holder = new ViewHolder();
            holder.folderName = convertView.findViewById(R.id.folderNameEditText); // pastikan ID TextView di list_folder.xml adalah folderNameTextView
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> folder = folderList.get(position);
        String folderName = folder.get(konfigurasi.TAG_FOLDER_NAME);

        holder.folderName.setText(folderName);

        // Klik item folder
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FolderDetail.class);
            // ⛔ PERBAIKAN: Key "FOLDER_ID" tidak ada di konfigurasi.java sebelumnya
            // SOLUSI: Pakai konfigurasi.KEY_FOLDER_ID supaya konsisten
            intent.putExtra(konfigurasi.KEY_FOLDER_ID, folder.get(konfigurasi.TAG_FOLDER_ID));
            intent.putExtra(konfigurasi.KEY_FOLDER_NAME, folderName);
            context.startActivity(intent);
        });

        return convertView;
    }
}
